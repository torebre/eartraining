package com.kjipo.scoregenerator

import com.kjipo.score.NoteType


fun getPitch(noteType: NoteType, octave: Int): Int {
    return 12 * octave + when (noteType) {
        NoteType.A -> 9
        NoteType.H -> 11
        NoteType.C -> 0
        NoteType.D -> 2
        NoteType.E -> 4
        NoteType.F -> 5
        NoteType.G -> 7
    }
}
