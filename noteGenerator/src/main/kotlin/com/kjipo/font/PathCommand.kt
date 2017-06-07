package com.kjipo.font

enum class PathCommand private constructor(val command: Char, val isAbsolute: Boolean) {
    VERTICAL_LINE_TO_RELATIVE('v', false),
    HORIZONAL_LINE_TO_RELATIVE('h', false),
    MOVE_TO_ABSOLUTE('M', true),
    MOVE_TO_RELATIVE('m', false),
    LINE_TO_RELATIVE('l', false),
    CURVE_TO_RELATIVE('c', false),
    CLOSE_PATH('z', false),
    SMOOTH_CURVE_TO_RELATIVE('s', false)
}