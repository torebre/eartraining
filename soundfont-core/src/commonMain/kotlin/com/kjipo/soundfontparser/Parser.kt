package com.kjipo.soundfontparser


expect suspend fun loadFile(path: String): ByteArray


object Parser {

    fun parse(soundFontData: ByteArray): List<Chunk> {
        val ckId = soundFontData.copyOfRange(0, 4)
        val ckIdString = transformToString(ckId)
        val chunkSize = extractSizeData(soundFontData.copyOfRange(4, 8))
        val chunks = mutableListOf<Chunk>()

        val topLevelChunk = Chunk(ckIdString, 0, chunkSize, null, false)
        chunks.add(topLevelChunk)
        parseChunk(12, topLevelChunk, chunks, soundFontData)

        assertFileIsValid(chunks)

        return chunks
    }

    private fun parseChunk(
        start: Int,
        parentChunk: Chunk? = null,
        chunks: MutableList<Chunk>,
        soundFontData: ByteArray
    ) {
        var currentIndex = start

        while (true) {
            if (currentIndex + 4 >= soundFontData.size) {
                break
            }

            val ckId = soundFontData.copyOfRange(currentIndex, currentIndex + 4)
            val ckIdString = transformToString(ckId)
            val ckSize = extractSizeData(soundFontData.copyOfRange(currentIndex + 4, currentIndex + 8))

            val isList = ckIdString == "LIST"
            val chunk = Chunk(
                ckIdString,
                currentIndex,
                ckSize,
                parentChunk,
                ckSize % 2 == 1,
                if (isList) transformToString(soundFontData.copyOfRange(currentIndex + 8, currentIndex + 12)) else null
            )
            chunks.add(chunk)

            if (isList) {
                parseChunk(currentIndex + 12, chunk, chunks, soundFontData)
            }

            currentIndex += 8 + ckSize + (ckSize % 2)
        }

    }


    private fun extractSizeData(ckSize: ByteArray): Int {
        return (ckSize[0].toInt() and 0xFF) or
                ((ckSize[1].toInt() and 0xFF) shl 8) or
                ((ckSize[2].toInt() and 0xFF) shl 16) or
                ((ckSize[3].toInt() and 0xFF) shl 24)
    }

    private fun assertFileIsValid(chunks: List<Chunk>) {
        val errors = mutableListOf<String>()
        val shdrChunk = chunks.find { it.chunkId == SHDR_CHUNK_NAME }

        if (shdrChunk == null) {
            errors.add("SHDR chunk not found")
        } else {
            if (shdrChunk.length - 8 % 46 == 0) {
                errors.add("SHDR chunk length is not a multiple of 46")
            }
        }

        if (errors.isNotEmpty()) {
            throw SoundfontParseException(errors)
        }

    }


}