package org.example

import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

object ImageUtils {
    val supportedFormats: String =
        ImageIO.getReaderFormatNames().map { name -> name.lowercase() }.distinct().joinToString(", ")

    fun loadImage(path: String): Result<BufferedImage> = runCatching {
        val file = File(path)
        require(file.exists()) { "File does not exist: $path" }
        require(file.isFile) { "Path is not a file: $path" }

        ImageIO.read(file)
            ?: throw IOException("Failed to load file: $path. Supported formats: $supportedFormats")
    }

    fun resizeImage(image: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
        val imageType = if (image.type == 0) BufferedImage.TYPE_INT_ARGB else image.type
        val resizedImage = BufferedImage(targetWidth, targetHeight, imageType)
        val graphics = resizedImage.createGraphics()
        graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null)
        graphics.dispose()
        return resizedImage
    }

    fun resizeImage(image: BufferedImage, targetWidth: Int, fontAspectRatio: Double): BufferedImage {
        val aspectRatio = image.height.toDouble() / image.width
        val targetHeight = (targetWidth * aspectRatio * fontAspectRatio).toInt().coerceAtLeast(1)
        return resizeImage(image, targetWidth = targetWidth, targetHeight = targetHeight)
    }
}
