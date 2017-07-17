package com.kjipo.svg

import com.kjipo.font.GlyphFactory
import com.kjipo.font.NoteType

class NoteElement(override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {
    val notes = mutableListOf<NOTE>()


    override fun toRenderingElement(): RenderingElement {

        // TODO

        return notes.map {
            when {
                it.duration == 24 -> GlyphFactory.getGlyph(NoteType.QUARTER_NOTE)
                it.duration == 48 -> GlyphFactory.getGlyph(NoteType.HALF_NOTE)
                else -> GlyphFactory.blankGlyph
            }
        }.let { RenderingElementImpl(it) }

    }






}