package com.kjipo.soundfontparser.serialization

fun shortArrayToLittleEndianBytes(values: ShortArray): ByteArray {
    val bytes = ByteArray(values.size * 2)
    for (i in values.indices) {
        val value = values[i].toInt()
        bytes[i * 2] = (value and 0xFF).toByte()
        bytes[i * 2 + 1] = ((value shr 8) and 0xFF).toByte()
    }
    return bytes
}

fun littleEndianBytesToShortArray(bytes: ByteArray): ShortArray {
    require(bytes.size % 2 == 0) { "Byte array length must be divisible by 2" }
    val values = ShortArray(bytes.size / 2)
    for (i in values.indices) {
        val low = bytes[i * 2].toInt() and 0xFF
        val high = bytes[i * 2 + 1].toInt() and 0xFF
        val combined = low or (high shl 8)
        val signed = if (combined >= 0x8000) combined - 0x10000 else combined
        values[i] = signed.toShort()
    }
    return values
}

fun intArrayToInt16LittleEndianBytes(values: IntArray): ByteArray {
    val bytes = ByteArray(values.size * 2)
    for (i in values.indices) {
        val value = values[i]
        require(value in -32768..32767) { "Value out of range for 16-bit PCM: $value" }
        bytes[i * 2] = (value and 0xFF).toByte()
        bytes[i * 2 + 1] = ((value shr 8) and 0xFF).toByte()
    }
    return bytes
}

fun int16LittleEndianBytesToIntArray(bytes: ByteArray): IntArray {
    require(bytes.size % 2 == 0) { "Byte array length must be divisible by 2" }
    val values = IntArray(bytes.size / 2)
    for (i in values.indices) {
        val low = bytes[i * 2].toInt() and 0xFF
        val high = bytes[i * 2 + 1].toInt() and 0xFF
        val combined = low or (high shl 8)
        val signed = if (combined >= 0x8000) combined - 0x10000 else combined
        values[i] = signed
    }
    return values
}
