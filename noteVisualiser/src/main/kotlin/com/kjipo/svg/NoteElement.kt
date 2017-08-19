package com.kjipo.svg

import com.kjipo.font.GlyphFactory
import com.kjipo.font.NoteType

class NoteElement(val pitch: Int, override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {
    val notes = mutableListOf<NOTE>()
    var bar: BAR? = null

    override fun toRenderingElement(): PositionedRenderingElement {
        val map = notes.map {
            when {
            // TODO Fix duration values. Should not be hardcoded here
                it.duration == 24 -> GlyphFactory.getGlyph(NoteType.QUARTER_NOTE)
                        .let { RenderingElementImpl(listOf(it), it.boundingBox) }
                it.duration == 48 -> GlyphFactory.getGlyph(NoteType.HALF_NOTE)
                        .let { RenderingElementImpl(listOf(it), it.boundingBox) }
                else -> throw IllegalArgumentException("Unhandled duration: ${it.duration}")
            }
        }

        // TODO Make better solution. Join rendering elements in a better way
        var finalRenderingElement: RenderingElementImpl
        if(map.size != 1) {
            finalRenderingElement = RenderingElementImpl(listOf(GlyphFactory.blankGlyph), GlyphFactory.blankGlyph.boundingBox)
            for (renderingElementImpl in map) {
                finalRenderingElement = RenderingElementImpl(finalRenderingElement, renderingElementImpl)
            }
        }
        else {
            finalRenderingElement = map[0]
        }

        finalRenderingElement.xPosition = xPosition
        finalRenderingElement.yPosition = yPosition

        return finalRenderingElement
    }

    fun getClef(): Clef {
        // TODO The clef can change withing a bar. This is not handled at present
        // Defaulting to G
        return bar?.clef ?: Clef.G
    }

}