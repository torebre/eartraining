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
    H,
    C,
    D,
    F,
    G,
    E
}


enum class NoteModifier {
    NONE,
    SHARP,
    FLAT
}


enum class Duration(val ticks: Int) {
    QUARTER(TICKS_PER_QUARTER_NOTE),
    HALF(2 * TICKS_PER_QUARTER_NOTE),
    WHOLE(4 * TICKS_PER_QUARTER_NOTE)
}