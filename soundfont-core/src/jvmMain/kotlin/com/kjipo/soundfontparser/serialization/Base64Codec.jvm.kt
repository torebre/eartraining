package com.kjipo.soundfontparser.serialization

import java.util.Base64

actual object Base64Codec {
    actual fun encode(bytes: ByteArray): String {
        return Base64.getEncoder().encodeToString(bytes)
    }

    actual fun decode(text: String): ByteArray {
        return Base64.getDecoder().decode(text)
    }
}
