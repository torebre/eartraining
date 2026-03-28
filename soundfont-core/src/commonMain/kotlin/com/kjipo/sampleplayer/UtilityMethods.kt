package com.kjipo.sampleplayer


internal fun createSamples(smplData: ByteArray): IntArray {
    val dataInSamples = IntArray(smplData.size / 2 - 4)
    for (i in 0 until dataInSamples.size) {
        val byteIndex = i * 2
        // Read 16-bit little-endian signed integer
        val low = smplData[byteIndex + 8].toInt() and 0xFF
        val high = smplData[byteIndex + 1 + 8].toInt()
        val sample16 = (high shl 8) or low

        // Convert to signed 16-bit
        val signedSample = if (sample16 > 32767) sample16 - 65536 else sample16
        dataInSamples[i] = signedSample
    }

    return dataInSamples
}
