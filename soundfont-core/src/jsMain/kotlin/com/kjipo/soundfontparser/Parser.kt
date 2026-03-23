package com.kjipo.soundfontparser

import kotlinx.browser.window
import kotlinx.coroutines.await
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get


actual suspend fun loadFile(path: String): ByteArray {
    val response = window.fetch(path).await()
    if (!response.ok) {
        throw Exception("Failed to load resource: $path")
    }
    val arrayBuffer = response.arrayBuffer().await()
    val uint8Array = Uint8Array(arrayBuffer)

    // Convert Uint8Array to ByteArray for common use
    return ByteArray(uint8Array.length) { it ->
//            uint8Array.get(it)
        uint8Array[it]
    }
}
