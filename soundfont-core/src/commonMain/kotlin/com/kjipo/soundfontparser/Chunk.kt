package com.kjipo.soundfontparser

class Chunk(
    val chunkId: String,
    val start: Int,
    val length: Int,
    val parentChunkId: Chunk?,
    val hasPaddingByte: Boolean,
    val listType: String? = null
) {

    override fun toString(): String {
        if (listType != null) {
            return "Chunk(chunkId='$chunkId', start=$start, length=$length, parentChunkId=${parentChunkId?.chunkId ?: "null"}, listType='$listType')"
        }
        return "Chunk(chunkId='$chunkId', start=$start, length=$length, parentChunkId=${parentChunkId?.chunkId ?: "null"}')"
    }

}