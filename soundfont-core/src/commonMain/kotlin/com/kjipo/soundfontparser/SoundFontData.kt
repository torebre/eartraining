package com.kjipo.soundfontparser


class SoundFontData(
    val shdrChunk: ShdrChunk,
    val instSubchunk: InstSubchunk,
    val phdrSubchunks: PhdrSubchunk,
    val smplChunk: SmplChunk?
) {

    fun getNumberOfSamples(): Int {
        return shdrChunk.shdrRecords.size
    }

}