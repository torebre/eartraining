package com.kjipo.soundfontparser


data class InstSubchunk(val sfInstRecords: List<SfInstRecord>) {

    override fun toString(): String {
        return "InstSubchunk(sfInstRecords=${sfInstRecords.joinToString("\n")})"
    }

}
