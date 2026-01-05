package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.awt.Transparency
import java.awt.image.BufferedImage

class Processor(val config: Config) {
    val lookup = IntArray(256) { brightness -> brightness * (config.characterRamp.length - 1) / 255 }
    val lookupInv = IntArray(256) { brightness -> (255 - brightness) * (config.characterRamp.length - 1) / 255 }

    fun colorToBrightness(source: Int): Int {
        val red = (source ushr 16) and 0xFF
        val green = (source ushr 8) and 0xFF
        val blue = source and 0xFF
        return ((config.colorWeights.redWeight * red + config.colorWeights.greenWeight * green + config.colorWeights.blueWeight * blue) ushr 8)
    }

    fun alphaBlend(source: Int, destination: Int): Int {
        val alpha = source ushr 24
        if (alpha == 255) return source
        if (alpha == 0) return destination
        val alphaInv = 255 - alpha

        val red1 = (source ushr 16) and 0xFF
        val green1 = (source ushr 8) and 0xFF
        val blue1 = source and 0xFF

        val red2 = (destination ushr 16) and 0xFF
        val green2 = (destination ushr 8) and 0xFF
        val blue2 = destination and 0xFF

        // https://arxiv.org/pdf/2202.02864
        var red3 = (red1 * alpha + red2 * alphaInv)
        red3 += 0x80
        red3 = (red3 + (red3 shr 8 )) shr 8
        var green3 = (green1 * alpha + green2 * alphaInv)
        green3 += 0x80
        green3 = (green3 + (green3 shr 8 )) shr 8
        var blue3 = (blue1 * alpha + blue2 * alphaInv)
        blue3 += 0x80
        blue3 = (blue3 + (blue3 shr 8 )) shr 8

        return (0xFF shl 24) or (red3 shl 16) or (green3 shl 8) or blue3
    }

    private fun processSequentially(imageWidth: Int, imageHeight: Int, getCharacter: (Int) -> Char): String {
        var index = 0
        val asciiWidth = imageWidth + 1
        val ascii = CharArray(asciiWidth * imageHeight)
        for (y in 0 until imageHeight) {
            val colorOffset = y * imageWidth
            for (x in 0 until imageWidth) {
                ascii[index++] = getCharacter(colorOffset + x)
            }
            ascii[index++] = '\n'
        }
        return String(ascii)
    }

    private suspend fun processParallelly(imageWidth: Int, imageHeight: Int, getCharacter: (Int) -> Char): String {
        val blockHeight = config.blockHeight
        val asciiWidth = imageWidth + 1
        val ascii = CharArray(asciiWidth * imageHeight)
        coroutineScope {
            for (startY in 0 until imageHeight step blockHeight) {
                val endY = minOf(startY + blockHeight, imageHeight)
                launch {
                    for (y in startY until endY) {
                        val asciiOffset = y * asciiWidth
                        val colorOffset = y * imageWidth
                        for (x in 0 until imageWidth) {
                            ascii[asciiOffset + x] = getCharacter(colorOffset + x)
                        }
                        ascii[asciiOffset + imageWidth] = '\n'
                    }
                }
            }
        }
        return String(ascii)
    }
    suspend fun process(image: BufferedImage, invert: Boolean): String {
        val imageWidth = image.width
        val imageHeight = image.height

        val ramp = config.characterRamp
        val background = if (invert) config.background xor 0x00FFFFFF else config.background
        val lookup = if (invert) lookupInv else lookup
        val colors = image.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth)
        val isOpaque = image.transparency == Transparency.OPAQUE

        val getCharacter: (Int) -> Char = if (isOpaque) {
                index: Int -> ramp[lookup[colorToBrightness(colors[index])]]
        } else {
                index: Int -> ramp[lookup[colorToBrightness(alphaBlend(colors[index], background))]]
        }

        val isSmall = imageWidth * imageHeight <= config.maxImageSizeForSequentialProcessing
        return if (isSmall) processSequentially(imageWidth, imageHeight, getCharacter) else processParallelly(imageWidth, imageHeight, getCharacter)
    }
}