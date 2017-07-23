package com.kjipo.svg

import com.kjipo.font.*


fun getExtraBarlines(pitch: Int): List<Int> {
    // TODO Hardcoded for G clef at present
    val (halfSteps, octave) = getPlacementAndOctave(pitch)
    val halfStepNumber = (octave - 5) * 12 + (halfSteps - 4)

    // TODO Mixing SVG and note layout here on the y-axis is confusing. Try to make clearer
    if (halfStepNumber < -3) {
        return generateSequence(-4, { it - 2 }).takeWhile { it >= halfStepNumber }.map { it * -DEFAULT_VERTICAL_NOTE_SPACING }.toList()
    }
    if (halfStepNumber > 7) {
        return generateSequence(8, { it + 2 }).takeWhile { it <= halfStepNumber }.map { it * -DEFAULT_VERTICAL_NOTE_SPACING }.toList()
    }
    return listOf()
}


fun calculateVerticalOffset(pitch: Int): Int {
    val (halfSteps, octave) = getPlacementAndOctave(pitch)
    return (5 - octave) * 12 * DEFAULT_VERTICAL_NOTE_SPACING + (4 - halfSteps) * DEFAULT_VERTICAL_NOTE_SPACING
}


private fun getPlacementAndOctave(pitch: Int): Pair<Int, Int> {
    // TODO Only handling key of C for now and not thinking much about sharps and flats
    val octave = pitch.div(12)
    val placementWithinOctave = pitch.rem(12)

    val halfSteps = when (placementWithinOctave) {
    // C
        0 -> 0
    // C#
        1 -> 0
    // D
        2 -> 1
    // D#
        3 -> 1
    // E
        4 -> 2
    // F
        5 -> 3
    // F#
        6 -> 3
    // G
        7 -> 4
    // G#
        8 -> 4
    // A
        9 -> 5
    // A#
        10 -> 5
    // H
        11 -> 6
        else -> throw IllegalArgumentException("Unexpected reminder: ${placementWithinOctave}")
    }
    return Pair(halfSteps, octave)
}

private fun drawLineSegment(yOffset: Int, boundingBox: BoundingBox): PathInterface {
    return translateGlyph(PathInterfaceImpl(listOf(PathElement(PathCommand.MOVE_TO_ABSOLUTE,
            listOf(boundingBox.xMax, yOffset.toDouble())),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(boundingBox.xMax))), 3),
            boundingBox.xMax.toInt(), 0)
}
