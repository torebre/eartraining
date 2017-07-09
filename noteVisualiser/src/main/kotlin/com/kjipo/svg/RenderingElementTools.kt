package com.kjipo.svg

import com.kjipo.font.*


fun createChord(notes: Collection<Note>): RenderingElement {
    return RenderingElement(notes.map { translateGlyph(GlyphFactory.getGlyph(it.type), 0, (7 - (it.pitch - 60)) * verticalNoteSpacing) })
}


fun calculateVerticalOffset(pitch: Int): Int {
    return (7 - (pitch - 60)) * verticalNoteSpacing
}

fun addAdditionalBarLines(note: Note): RenderingElement {
    // Assuming G-clef
    val glyph = GlyphFactory.getGlyph(note.type)

    // TODO Does not handle C sharp correctly
    return RenderingElement(generateSequence(60, { it - 2})
            .takeWhile { it >= note.pitch }
            .map { drawLineSegment(calculateVerticalOffset(it), glyph.boundingBox) }.toList())
}


private fun drawLineSegment(yOffset: Int, boundingBox: BoundingBox): PathInterface {
    return translateGlyph(PathInterfaceImpl(listOf(PathElement(PathCommand.MOVE_TO_ABSOLUTE,
            listOf(boundingBox.xMax, yOffset.toDouble())),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(boundingBox.xMax))), 3),
            boundingBox.xMax.toInt(), 0)
}
