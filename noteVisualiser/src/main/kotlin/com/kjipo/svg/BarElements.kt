package com.kjipo.svg

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
    E,
    F,
    G
}



enum class NoteModifier {
    NONE,
    SHARP,
    FLAT
}