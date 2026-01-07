package org.example

import java.io.File
import java.io.IOException


object AsciiUtils {
    fun loadAscii(path: String): Result<String> = runCatching {
        val file = File(path)
        require(file.exists()) { "File does not exist: $path" }
        require(file.isFile) { "Path is not a file: $path" }
        try {
            file.readText()
        } catch (e: IOException) {
            throw IOException("Failed to load file: $path.", e)
        }
    }

    fun saveAscii(path: String, ascii: String) {
        File(path).writeText(ascii)
    }
}