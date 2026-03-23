package com.kjipo.soundfontparser


internal fun transformToString(inputData: ByteArray): String {
    return CharArray(inputData.size, {
        Char(inputData[it].toInt())
    }).concatToString()
}

internal fun extractDword(data: ByteArray, offset: Int): Int {
    return (data[offset].toInt() and 0xFF) or
            ((data[offset + 1].toInt() and 0xFF) shl 8) or
            ((data[offset + 2].toInt() and 0xFF) shl 16) or
            ((data[offset + 3].toInt() and 0xFF) shl 24)
}

internal fun extractWord(data: ByteArray, offset: Int): Int {
    return (data[offset].toInt() and 0xFF) or
            ((data[offset + 1].toInt() and 0xFF) shl 8)
}

internal fun extractByte(data: ByteArray, offset: Int): Int {
    return data[offset].toInt() and 0xFF
}


fun printChunks(chunks: List<Chunk>) {
    val childrenMap = chunks.groupBy { it.parentChunkId }
    val topLevelChunks = chunks.filter { it.parentChunkId == null }

    for (topLevelChunk in topLevelChunks) {
        println(topLevelChunk)
        childrenMap[topLevelChunk]?.let { children ->
            printChunks(children, childrenMap, 0)
        }
    }
}


private fun printChunks(chunkList: List<Chunk>, childrenMap: Map<Chunk?, List<Chunk>>, numberOfTabs: Int) {
    for (chunk in chunkList) {
        println("${"\t".repeat(numberOfTabs)}$chunk")
        childrenMap[chunk]?.let { printChunks(it, childrenMap, numberOfTabs + 1) }
    }
}

