package com.kjipo.soundfontparser

data class PgenRecord(
    val generatorData: GeneratorData?,
    val rangeByteLow: Int,
    val rangeByteHigh: Int,
    val shAmount: Int,
    val wAmount: Int,
) {

    override fun toString(): String {
        return "PgenRecord(generatorData=$generatorData, rangeByteLow=$rangeByteLow, rangeByteHigh=$rangeByteHigh, shAmount=$shAmount, wAmount=$wAmount)"
    }

}
