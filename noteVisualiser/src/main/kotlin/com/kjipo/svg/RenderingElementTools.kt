package com.kjipo.svg

import com.kjipo.font.*


fun createChord(notes: Collection<Note>): RenderingElementImpl {

    // TODO Set correct bounding box
    return RenderingElementImpl(notes.map { translateGlyph(GlyphFactory.getGlyph(it.type), 0, (7 - (it.pitch - 60)) * DEFAULT_VERTICAL_NOTE_SPACING) }, BoundingBox(0.0, 0.0, 0.0, 0.0))
}


fun calculateVerticalOffset(pitch: Int): Int {
    return (7 - (pitch - 60)) * DEFAULT_VERTICAL_NOTE_SPACING
}

fun addAdditionalBarLines(note: Note): RenderingElementImpl {
    // Assuming G-clef
    val glyph = GlyphFactory.getGlyph(note.type)

    // TODO Does not handle C sharp correctly
    // TODO Set correct bounding box
    return RenderingElementImpl(generateSequence(60, { it - 2})
            .takeWhile { it >= note.pitch }
            .map { drawLineSegment(calculateVerticalOffset(it), glyph.boundingBox) }.toList(), BoundingBox(0.0, 0.0, 0.0, 0.0))
}


private fun drawLineSegment(yOffset: Int, boundingBox: BoundingBox): PathInterface {
    return translateGlyph(PathInterfaceImpl(listOf(PathElement(PathCommand.MOVE_TO_ABSOLUTE,
            listOf(boundingBox.xMax, yOffset.toDouble())),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(boundingBox.xMax))), 3),
            boundingBox.xMax.toInt(), 0)
}
