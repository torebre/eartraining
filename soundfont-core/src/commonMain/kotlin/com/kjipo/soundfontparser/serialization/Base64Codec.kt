package com.kjipo.soundfontparser.serialization

expect object Base64Codec {
    fun encode(bytes: ByteArray): String
    fun decode(text: String): ByteArray
}
