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
    val path by option("--path").required()
    val width by option("--width").int().default(100)
    val height by option("--height").int()
    val invert by option("--invert").flag()
    val image by lazy { ImageUtils.loadImage(path).getOrThrow() }
    val config = Config.default()
    override fun run() {
        val resizedImage = height?.let {
            ImageUtils.resizeImage(image, width, it)
        } ?: ImageUtils.resizeImage(image, width, config.fontAspectRatio)
        val processor = Processor(config)
        runBlocking {
            val ascii = processor.process(resizedImage, invert)
            println(ascii)
        }
    }
}

fun main(args: Array<String>) = Main().main(args)

