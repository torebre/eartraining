package com.kjipo.soundfontparser

import java.io.File


actual suspend fun loadFile(path: String): ByteArray {
    return try {
        // Try loading as a file from filesystem
        val file = File(path)
        if (file.exists()) {
            return file.readBytes()
        }

        // Try loading as a resource from classpath
        val resourceStream = Thread.currentThread().contextClassLoader.getResourceAsStream(path)
            ?: throw Exception("Resource not found: $path")
        resourceStream.use { it.readBytes() }
    } catch (e: Exception) {
        throw Exception("Failed to load resource: $path - ${e.message}", e)
    }
}
