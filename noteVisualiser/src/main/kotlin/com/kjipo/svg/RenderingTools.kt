package com.kjipo.svg

import com.kjipo.font.*
import org.w3c.dom.Element


fun addExtraBarLines(noteElement: NoteElement): ScoreRenderingElement? {
    return when(noteElement.getClef()) {
        // TODO Only handling G-clef for now
        Clef.G -> addExtraBarLinesForGClef(noteElement)
        else -> null
    }
}

private fun addExtraBarLinesForGClef(noteElement: NoteElement): ScoreRenderingElement? {
    // TODO Also need to check above bar
    if(noteElement.pitch < 61) {
        // TODO Does not handle sharps and flats correctly
        val noteRenderingElement = noteElement.toRenderingElement()

        val yPositions = generateSequence(60, {it - 2})
                .takeWhile { it >= noteElement.pitch }
                .map { calculateVerticalOffset(it) }
                .toList()

        return ExtraBarLinesElement(noteElement.xPosition, 0, yPositions,
                noteRenderingElement.boundingBox.xMax.minus(noteRenderingElement.boundingBox.xMin).toInt())
    }

    return null
}


fun addStem(boundingBox: BoundingBox, stemUp: Boolean = true): PathInterface {
    val yEnd = if (stemUp) -DEFAULT_STEM_HEIGHT.toDouble() else DEFAULT_STEM_HEIGHT.toDouble()
    return translateGlyph(PathInterfaceImpl(listOf(PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(0.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, yEnd))), 3), boundingBox.xMax.toInt(), 0)
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
