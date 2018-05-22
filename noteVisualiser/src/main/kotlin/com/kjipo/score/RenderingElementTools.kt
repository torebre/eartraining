package com.kjipo.score


fun getExtraBarlines(note: NoteType, octave: Int): List<Int> {
    // TODO Hardcoded for G clef at present
    val halfSteps = getPlacementAndOctave(note)
    val halfStepNumber = (octave - 5) * 7 + (halfSteps - 4)

    // TODO Mixing SVG and note layout here on the y-axis is confusing. Try to make clearer
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

private fun getPlacementAndOctave(note: NoteType): Int {
    // TODO Only handling key of C for now and not thinking much about sharps and flats

    return when (note) {
        NoteType.C -> 0
        NoteType.D -> 1
        NoteType.E -> 2
        NoteType.F -> 3
        NoteType.G -> 4
        NoteType.A -> 5
        NoteType.H -> 6
    }
}