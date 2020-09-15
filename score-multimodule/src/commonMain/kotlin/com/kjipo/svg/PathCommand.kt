package com.kjipo.svg

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PathCommand constructor(val command: Char, val isAbsolute: Boolean) {
    @SerialName("v")
    VERTICAL_LINE_TO_RELATIVE('v', false),

    @SerialName("V")
    VERTICAL_LINE_TO_ABSOLUTE('V', true),

    @SerialName("h")
    HORIZONAL_LINE_TO_RELATIVE('h', false),

    @SerialName("M")
    MOVE_TO_ABSOLUTE('M', true),

    @SerialName("m")
    MOVE_TO_RELATIVE('m', false),

    @SerialName("l")
    LINE_TO_RELATIVE('l', false),

    @SerialName("c")
    CURVE_TO_RELATIVE('c', false),

    @SerialName("C")
    CURVE_TO_ABSOLUTE('C', true),

    @SerialName("z")
    CLOSE_PATH('z', false),

    @SerialName("s")
    SMOOTH_CURVE_TO_RELATIVE('s', false)
}
