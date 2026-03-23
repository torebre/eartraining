package com.kjipo.soundfontparser


data class PhdrSubchunk(val phdrRecords: List<PhdrRecord>) {

    override fun toString(): String {
        return "PhdrSubchunk(phdrRecords=${phdrRecords.joinToString("\n")})"
    }

}
