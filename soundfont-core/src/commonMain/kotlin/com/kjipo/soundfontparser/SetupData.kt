package com.kjipo.soundfontparser


object SetupData {

    private const val SHDR_RECORD_SIZE = 46
    private const val SF_INST_RECORD_SIZE = 22
    private const val PHDR_RECORD_SIZE = 38
    private const val PMOD_RECORD_SIZE = 10


    fun setupData(chunks: List<Chunk>, soundFontData: ByteArray): SoundFontData {
        val shdrChunk = chunks.find { it.chunkId == SHDR_CHUNK_NAME }?.let { shdrChunk ->
            handleShdrChunk(shdrChunk, soundFontData)
        }

        val instSubchunk = chunks.find { it.chunkId == INST_CHUNK_NAME }?.let { instSubchunk ->
            handleInstSubchunk(instSubchunk, soundFontData)
        }
//        println(instSubchunk)

        val phdrSubchunks = chunks.find { it.chunkId == PHDR_CHUNK_NAME }?.let { phdrSubchunk ->
            handlePhdrSubchunk(phdrSubchunk, soundFontData)
        }
//        println(phdrSubchunks)

        val pgenChunk = chunks.find { it.chunkId == PGEN_CHUNK_NAME }?.let { pgenChunk ->
            handlePgenChunk(pgenChunk, soundFontData)

        }

        val smplChunk = chunks.find { it.chunkId == SMPL_CHUNK_NAME }?.let { smplChunk ->
            SmplChunk(soundFontData.copyOfRange(smplChunk.start, smplChunk.start + smplChunk.length))
        }

        val pmodChunk = chunks.find { it.chunkId == PMOD_CHUNK_NAME }?.let { pmodChunk ->
           handlePmodChunk(pmodChunk, soundFontData)
        }

        return if (smplChunk == null
            || shdrChunk == null
            || instSubchunk == null
            || phdrSubchunks == null
            || pgenChunk == null
            || pmodChunk == null) {
            val missingChunks = mutableListOf<String>()
            if (shdrChunk == null) {
                missingChunks.add("SHDR chunk")
            }

            if (instSubchunk == null) {
                missingChunks.add("INST chunk")
            }

            if (phdrSubchunks == null) {
                missingChunks.add("PHDR chunk")
            }

            if(pmodChunk == null) {
                missingChunks.add("PMOD chunk")
            }

            if(pgenChunk == null) {
                missingChunks.add("PGEN chunk")
            }

            throw SoundfontParseException("One or more missing chunks: ${missingChunks.joinToString(",")}")

        } else {
            // TODO Add PMOD and PGEN chunk
            SoundFontData(shdrChunk, instSubchunk, phdrSubchunks, smplChunk)
        }

    }

    private fun handlePmodChunk(pmodChunk: Chunk, soundFontData: ByteArray) {
        val pmodData = soundFontData.copyOfRange(pmodChunk.start, pmodChunk.start + pmodChunk.length)
        var recordOffset = 8

        while (recordOffset < pmodChunk.length - PMOD_RECORD_SIZE) {
            val sfModSrcOper = extractWord(pmodData, recordOffset)
            recordOffset += 2

            val sfModDestOper = extractWord(pmodData, recordOffset)
            recordOffset += 2

            val modAmount = extractWord(pmodData, recordOffset)
            recordOffset += 2

            val sfModAmtSrcOper = extractWord(pmodData, recordOffset)
            recordOffset += 2

            val sfModTransOper = extractWord(pmodData, recordOffset)
            recordOffset += 2

            println("PMOD chunk: $sfModSrcOper, $sfModDestOper, $modAmount, $sfModAmtSrcOper, $sfModTransOper")

            // TODO

        }

    }



    /**
     * https://www.synthfont.com/sfspec24.pdf
     *
     * Section 7.5 The PGEN Sub-chunk
     *
     */
    fun handlePgenChunk(pgenChunk: Chunk, soundFontData: ByteArray) {
        val pgenData = soundFontData.copyOfRange(pgenChunk.start, pgenChunk.start + pgenChunk.length)
        val pgenRecords = mutableListOf<PgenRecord>()
        var recordOffset = 8

        while (recordOffset < pgenChunk.length - 4) {
            val sfGenOpen = extractWord(pgenData, recordOffset)
            recordOffset += 2

            val generatorData = Generators.getGeneratorData(sfGenOpen)
            val rangeByteLow = extractByte(pgenData, recordOffset)
            recordOffset += 1
            val rangeByteHigh = extractByte(pgenData, recordOffset)
            recordOffset += 1

            val shAmount = extractWord(pgenData, recordOffset)
            recordOffset += 2
            val wAmount = extractWord(pgenData, recordOffset)
            recordOffset += 2

            val pgenRecord = PgenRecord(generatorData, rangeByteLow, rangeByteHigh, shAmount, wAmount)

            pgenRecords.add(pgenRecord)
        }

    }

    /**
     * https://www.synthfont.com/sfspec24.pdf
     *
     * Section 7.6 The INST Sub-chunk
     */
    private fun handleInstSubchunk(instSubchunk: Chunk, soundFontData: ByteArray): InstSubchunk {
        val instData = soundFontData.copyOfRange(instSubchunk.start, instSubchunk.start + instSubchunk.length)

        return InstSubchunk(
            (0..<((instSubchunk.length - 8) / SF_INST_RECORD_SIZE))
                .map { it * SF_INST_RECORD_SIZE }
                .map { it + 8 }
                .map { instData.copyOfRange(it, it + SF_INST_RECORD_SIZE) }
                .map { parseInstRecord(it) })
    }

    private fun parseInstRecord(recordData: ByteArray): SfInstRecord {
        return SfInstRecord(
            transformToString(recordData.copyOfRange(0, 20)).trimEnd('\u0000'),
            extractWord(recordData, 20)
        )
    }

    /**
     * https://www.synthfont.com/sfspec24.pdf
     *
     * Section 7.2 The PHDR Sub-chunk
     */
    private fun handlePhdrSubchunk(phdrSubchunk: Chunk, soundFontData: ByteArray): PhdrSubchunk {
        val phdrData = soundFontData.copyOfRange(phdrSubchunk.start, phdrSubchunk.start + phdrSubchunk.length)

        return PhdrSubchunk(
            (0..<((phdrSubchunk.length - 8) / PHDR_RECORD_SIZE))
                .map { it * PHDR_RECORD_SIZE }
                .map { it + 8 }
                .map { phdrData.copyOfRange(it, it + PHDR_RECORD_SIZE) }
                .map { parsePhdrRecord(it) })
    }

    private fun parsePhdrRecord(recordData: ByteArray): PhdrRecord {
        return PhdrRecord(
            achPresetName = transformToString(recordData.copyOfRange(0, 20)).trimEnd('\u0000'),
            wPreset = extractWord(recordData, 20),
            wBank = extractWord(recordData, 22),
            wPresetBagNdx = extractWord(recordData, 24),
            dwLibrary = extractDword(recordData, 26),
            dwGenre = extractDword(recordData, 30),
            dwMorphology = extractDword(recordData, 34)
        )
    }

    /**
     * https://www.synthfont.com/sfspec24.pdf
     *
     * Section 7.10 The SHDR Sub-chunk
     */
    private fun handleShdrChunk(chunk: Chunk, soundFontData: ByteArray): ShdrChunk {
        val shdrData = soundFontData.copyOfRange(chunk.start, chunk.start + chunk.length)

        val shdrRecords = mutableListOf<ShdrRecord>()
        var recordOffset = 8
        while (recordOffset < chunk.length - SHDR_RECORD_SIZE) {
            val recordData = shdrData.copyOfRange(recordOffset, recordOffset + SHDR_RECORD_SIZE)
            shdrRecords.add(parseShrdRecord(recordData))
            recordOffset += SHDR_RECORD_SIZE

//            println(shdrRecord)

        }

        return ShdrChunk(shdrRecords)
    }


    private fun parseShrdRecord(shdrRecord: ByteArray): ShdrRecord {
        return ShdrRecord(
            achSampleName = transformToString(shdrRecord.copyOfRange(0, 20)).trimEnd('\u0000'),
            dwStart = extractDword(shdrRecord, 20),
            dwEnd = extractDword(shdrRecord, 24),
            dwStartloop = extractDword(shdrRecord, 28),
            dwEndloop = extractDword(shdrRecord, 32),
            dwSampleRate = extractDword(shdrRecord, 36),
            byOriginalPitch = shdrRecord[40],
            chPitchCorrection = extractByte(shdrRecord, 41),
            wSampleLink = extractWord(shdrRecord, 42),
            sfSampleType = SampleType.fromValue(extractWord(shdrRecord, 44))
        )
    }


}