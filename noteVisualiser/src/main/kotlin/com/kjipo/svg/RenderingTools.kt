package com.kjipo.svg

import com.kjipo.font.*
import org.w3c.dom.Element


fun addExtraBarLines(noteElement: NoteElement): ScoreRenderingElement? {
    return when (noteElement.getClef()) {
    // TODO Only handling G-clef for now
        Clef.G -> addExtraBarLinesForGClef(noteElement)
        else -> null
    }
}

private fun addExtraBarLinesForGClef(noteElement: NoteElement): ScoreRenderingElement? {
    getExtraBarlines(noteElement.note, noteElement.octave).let {
        if (it.isEmpty()) {
            return null
        }
        val noteRenderingElement = noteElement.toRenderingElement()
        return ExtraBarLinesElement(noteElement.xPosition, 0, it,
                noteRenderingElement.boundingBox.xMax.minus(noteRenderingElement.boundingBox.xMin).toInt())
    }
}


fun addStem(noteHeadBoundingBox: BoundingBox, stemUp: Boolean = true): PathInterface {
    val yEnd = if (stemUp) -DEFAULT_STEM_HEIGHT.toDouble() else DEFAULT_STEM_HEIGHT.toDouble()
    return translateGlyph(PathInterfaceImpl(listOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(0.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, yEnd)),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(-2.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, -yEnd)),
            PathElement(PathCommand.CLOSE_PATH, listOf())), 1),
            noteHeadBoundingBox.xMax.toInt(), 0)
}

fun addBeam(xMin: Int, yMin: Int, xMax: Int, yMax: Int, startX: Int, startY: Int): PathInterface {
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


fun drawBarLines(element: Element, xStart: Int, gLine: Int) {
    val width = DEFAULT_BAR_WIDTH
    val spaceBetweenLines = 2 * DEFAULT_VERTICAL_NOTE_SPACING

    var x = xStart
    var y = gLine - spaceBetweenLines * 3

    drawLine(x, y, x, y + 4 * spaceBetweenLines, element, 1)
    drawLine(x + width, y, x + width, y + 4 * spaceBetweenLines, element, 1)
    for (i in 0..4) {
        drawLine(x, y, x + width, y, element, 1)
        y += spaceBetweenLines
    }
}
