package com.kjipo.score

import com.kjipo.svg.*


fun addExtraBarLinesForGClef(note: NoteType, octave: Int, xPosition: Int, yPosition: Int, boundingBoxMin: Int, boundingBoxMax: Int): ScoreRenderingElement? {
    getExtraBarlines(note, octave).let {
        if (it.isEmpty()) {
            return null
        }
        return ExtraBarLinesElement(xPosition, yPosition, it,
                boundingBoxMin.plus(EXTRA_BAR_LINE_LEFT_PADDING),
                boundingBoxMax.plus(EXTRA_BAR_LINE_RIGHT_PADDING))
    }
}

fun addStem(noteHeadBoundingBox: BoundingBox, stemUp: Boolean = true): PathInterfaceImpl {
    val yEnd = if (stemUp) -DEFAULT_STEM_HEIGHT.toDouble() else DEFAULT_STEM_HEIGHT.toDouble()
    return translateGlyph(PathInterfaceImpl(listOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(0.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, yEnd)),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(-2.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, -yEnd)),
            PathElement(PathCommand.CLOSE_PATH, listOf())), 1),
            noteHeadBoundingBox.xMax.toInt(), 0)
}

fun addBeam(xMin: Int, yMin: Int, xMax: Int, yMax: Int, startX: Int, startY: Int): PathInterfaceImpl {
    return translateGlyph(PathInterfaceImpl(listOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(0.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(DEFAULT_BEAM_HEIGHT.toDouble())),
            PathElement(PathCommand.LINE_TO_RELATIVE, listOf(0.0,
                    yMin.toDouble(),
                    xMax.toDouble() - xMin.toDouble(),
                    yMax.toDouble())),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(-DEFAULT_BEAM_HEIGHT.toDouble())),
            PathElement(PathCommand.CLOSE_PATH, emptyList())), 1),
            startX, startY)
}

