package com.kjipo.score

import com.kjipo.svg.*


fun addExtraBarLinesForGClef(note: GClefNoteLine, octave: Int, xPosition: Int, yPosition: Int, boundingBoxMin: Int, boundingBoxMax: Int): ScoreRenderingElement? {
    getExtraBarlines(note, octave).let {
        if (it.isEmpty()) {
            return null
        }
        return ExtraBarLinesElement(xPosition, yPosition, it,
                boundingBoxMin.plus(EXTRA_BAR_LINE_LEFT_PADDING),
                boundingBoxMax.plus(EXTRA_BAR_LINE_RIGHT_PADDING))
    }
}

fun addStem(noteHeadBoundingBox: BoundingBox, stemUp: Boolean = true) = addStem(noteHeadBoundingBox.xMax.toInt(), 0, DEFAULT_STEM_WIDTH, DEFAULT_STEM_HEIGHT, stemUp)

fun addStem(xTranslate: Int, yTranslate: Int, stemWidth: Int, stemHeight: Int = DEFAULT_STEM_HEIGHT, stemUp: Boolean = true): PathInterfaceImpl {
    val yEnd = if (stemUp) -stemHeight.toDouble() else stemHeight.toDouble()
    return translateGlyph(PathInterfaceImpl(listOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(0.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, yEnd)),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(-stemWidth.toDouble(), 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, -yEnd)),
            PathElement(PathCommand.CLOSE_PATH, listOf())), 1),
            xTranslate, yTranslate)
}

fun addBeam(xMin: Double, yMin: Double, xMax: Double, yMax: Double): PathInterfaceImpl {
    return PathInterfaceImpl(listOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xMin, yMin)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(DEFAULT_BEAM_HEIGHT.toDouble())),
            PathElement(PathCommand.LINE_TO_RELATIVE, listOf(0.0,
                    0.0,
                    xMax,
                    yMax)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(-DEFAULT_BEAM_HEIGHT.toDouble())),
            PathElement(PathCommand.CLOSE_PATH, emptyList())), 1)
}

