package org.example

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.runBlocking

class Main : CliktCommand() {
    val input by option("--input", help = "Path to the source file").required()
    val output by option("--output", help = "Path to the destination file; prints to console if omitted")
    val width by option("--width", help = "Target width for the output").int().default(100)
    val height by option("--height", help = "Target height for the output").int()
    val invert by option("--invert", help = "Invert the luminance of the output")
        .flag()
    val image by lazy { ImageUtils.loadImage(input).getOrThrow() }
    val config = Config.default()
    override fun run() {
        val resizedImage = height?.let {
            ImageUtils.resizeImage(image, width, it)
        } ?: ImageUtils.resizeImage(image, width, config.fontAspectRatio)
        val processor = Processor(config)
        runBlocking {
            val ascii = processor.process(resizedImage, invert)
            output?.let {
                AsciiUtils.saveAscii(it, ascii)
            } ?: println(ascii)
        }
    }
}

fun main(args: Array<String>) = Main().main(args)

