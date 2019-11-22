package com.kjipo.score

enum class Clef {
    G,
    NONE
}


data class TimeSignature(val nominator: Int, val denominator: Int)


enum class Key {
    C,
    NONE
}


enum class NoteType {
    A,
    A_SHARP,
    H,
    C,
    C_SHARP,
    D,
    D_SHARP,
    E,
    F,
    F_SHARP,
    G,
    G_SHARP
}

enum class Duration(val ticks: Int) {
    ZERO(0),
    EIGHT(TICKS_PER_QUARTER_NOTE / 2),
    QUARTER(TICKS_PER_QUARTER_NOTE),
    HALF(2 * TICKS_PER_QUARTER_NOTE),
    WHOLE(4 * TICKS_PER_QUARTER_NOTE)
}