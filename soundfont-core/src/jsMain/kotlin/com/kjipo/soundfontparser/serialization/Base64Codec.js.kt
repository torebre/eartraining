package com.kjipo.soundfontparser.serialization

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

actual object Base64Codec {
    actual fun encode(bytes: ByteArray): String {
        val uint8Array = Uint8Array(bytes.toTypedArray())
        var binary = ""
        for (i in 0 until uint8Array.length) {
            binary += uint8Array[i].toInt().toChar()
        }
        return btoa(binary)
    }

    actual fun decode(text: String): ByteArray {
        val binary = atob(text)
        val bytes = ByteArray(binary.length)
        for (i in binary.indices) {
            bytes[i] = binary[i].code.toByte()
        }
        return bytes
    }
}

private external fun btoa(s: String): String
private external fun atob(s: String): String
