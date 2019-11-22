package com.kjipo.score


fun getExtraBarlines(note: NoteType, octave: Int): List<Int> {
    // TODO Hardcoded for G clef at present
    val halfSteps = getPlacementAndOctave(note)
    val halfStepNumber = (octave - 5) * 7 + (halfSteps - 4)

    if (halfStepNumber < -3) {
        return generateSequence(-4, { it - 2 }).takeWhile { it >= halfStepNumber }.map { it * -DEFAULT_VERTICAL_NOTE_SPACING }.toList()
    }
    if (halfStepNumber > 7) {
        return generateSequence(8, { it + 2 }).takeWhile { it <= halfStepNumber }.map { it * -DEFAULT_VERTICAL_NOTE_SPACING }.toList()
    }
    return listOf()
}


fun calculateVerticalOffset(note: NoteType, octave: Int): Int {
    val halfSteps = getPlacementAndOctave(note)
    return (5 - octave) * 7 * DEFAULT_VERTICAL_NOTE_SPACING + (4 - halfSteps) * DEFAULT_VERTICAL_NOTE_SPACING
}

/**
 * Retrieves the line the relative to C the note should be placed on
 */
private fun getPlacementAndOctave(note: NoteType): Int {
    return when (note) {
        NoteType.C, NoteType.C_SHARP -> 0
        NoteType.D, NoteType.D_SHARP -> 1
        NoteType.E -> 2
        NoteType.F, NoteType.F_SHARP -> 3
        NoteType.G, NoteType.G_SHARP -> 4
        NoteType.A, NoteType.A_SHARP -> 5
        NoteType.H -> 6
    }
}