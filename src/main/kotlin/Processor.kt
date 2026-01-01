package org.example

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage

class Processor(val config: Config) {
    val lookup = IntArray(256) { brightness -> brightness * (config.characterRamp.length - 1) / 255 }
    val invertedLookup = IntArray(256) { brightness -> (255 - brightness) * (config.characterRamp.length - 1) / 255 }

    fun colorToBrightness(color: Int): Int {
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        return ((config.colorWeights.redWeight * red + config.colorWeights.greenWeight * green + config.colorWeights.blueWeight * blue) shr 8)
    }

    private fun processSequentially(image: BufferedImage, invert: Boolean): String {
        val imageWidth = image.width
        val imageHeight = image.height
        val lookup = if (invert) invertedLookup else lookup
        val ramp = config.characterRamp
        val colors = image.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth)
        val ascii = CharArray((imageWidth + 1) * imageHeight)
        var index = 0
        for (y in 0 until imageHeight) {
            val colorOffset = y * imageWidth
            for (x in 0 until imageWidth) {
                val alpha = (colors[colorOffset + x] shr 24) and 0xff
                ascii[index++] = if (alpha == 0) {
                    ' '
                } else {
                    ramp[lookup[colorToBrightness(colors[colorOffset + x])]]
                }
            }
            ascii[index++] = '\n'
        }
        return String(ascii)
    }

    private suspend fun processParallelly(image: BufferedImage, invert: Boolean): String {
        val imageWidth = image.width
        val imageHeight = image.height
        val lookup = if (invert) invertedLookup else lookup
        val ramp = config.characterRamp
        val blockHeight = config.blockHeight
        val colors = image.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth)
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
                            val alpha = (colors[colorOffset + x] shr 24) and 0xff
                            ascii[asciiOffset + x] = if (alpha == 0) {
                                ' '
                            } else {
                                ramp[lookup[colorToBrightness(colors[colorOffset + x])]]
                            }
                        }
                        ascii[asciiOffset + imageWidth] = '\n'
                    }
                }
            }
        }
        return String(ascii)
    }

    suspend fun process(image: BufferedImage, invert: Boolean = false): String {
        return if (image.width * image.height <= config.maxImageSizeForSequentialProcessing) processSequentially(
            image,
            invert
        ) else processParallelly(image, invert)
    }
}