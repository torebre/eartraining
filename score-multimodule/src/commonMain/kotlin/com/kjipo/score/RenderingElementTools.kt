package com.kjipo.score


fun getExtraBarlines(note: GClefNoteLine, octave: Int): List<Int> {
    // TODO Hardcoded for G clef at present
    val halfSteps = getPlacementAndOctave(note)
    val halfStepNumber = (octave - 5) * 7 + (halfSteps - 4)

    if (halfStepNumber < -3) {
        return generateSequence(-4, { it - 2 }).takeWhile { it >= halfStepNumber }
            .map { it * -DEFAULT_VERTICAL_NOTE_SPACING }.toList()
    }
    if (halfStepNumber > 7) {
        return generateSequence(8, { it + 2 }).takeWhile { it <= halfStepNumber }
            .map { it * -DEFAULT_VERTICAL_NOTE_SPACING }.toList()
    }
    return listOf()
}


fun calculateVerticalOffset(note: GClefNoteLine, octave: Int): Double {
    val halfSteps = getPlacementAndOctave(note)
    return ((5 - octave) * 7 * DEFAULT_VERTICAL_NOTE_SPACING + (4 - halfSteps) * DEFAULT_VERTICAL_NOTE_SPACING).toDouble()
}

/**
 * Retrieves the line the relative to C the note should be placed on
 */
private fun getPlacementAndOctave(note: GClefNoteLine): Int {
    return when (note) {
        GClefNoteLine.C -> 0
        GClefNoteLine.D -> 1
        GClefNoteLine.E -> 2
        GClefNoteLine.F -> 3
        GClefNoteLine.G -> 4
        GClefNoteLine.A -> 5
        GClefNoteLine.H -> 6
    }
}