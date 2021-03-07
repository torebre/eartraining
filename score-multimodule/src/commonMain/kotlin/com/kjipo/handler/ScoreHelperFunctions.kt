package com.kjipo.handler

import com.kjipo.score.Accidental
import com.kjipo.score.GClefNoteLine
import com.kjipo.score.NoteType

object ScoreHelperFunctions {

    internal fun transformToNoteAndAccidental(noteType: NoteType): Pair<GClefNoteLine, Accidental?> {
        return when (noteType) {
            NoteType.A -> Pair(GClefNoteLine.A, null)
            NoteType.A_SHARP -> Pair(GClefNoteLine.A, Accidental.SHARP)
            NoteType.H -> Pair(GClefNoteLine.H, null)
            NoteType.C -> Pair(GClefNoteLine.C, null)
            NoteType.C_SHARP -> Pair(GClefNoteLine.C, Accidental.SHARP)
            NoteType.D -> Pair(GClefNoteLine.D, null)
            NoteType.D_SHARP -> Pair(GClefNoteLine.D, Accidental.SHARP)
            NoteType.E -> Pair(GClefNoteLine.E, null)
            NoteType.F -> Pair(GClefNoteLine.F, null)
            NoteType.F_SHARP -> Pair(GClefNoteLine.F, Accidental.SHARP)
            NoteType.G -> Pair(GClefNoteLine.G, null)
            NoteType.G_SHARP -> Pair(GClefNoteLine.G, Accidental.SHARP)
        }

    }

}