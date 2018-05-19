package com.kjipo.svg

import com.kjipo.font.GlyphFactory
import com.kjipo.font.NoteType

class RestElement(override val duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int): TemporalElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val noteRenderedElement = when (duration) {
            Duration.QUARTER -> GlyphFactory.getRest(NoteType.QUARTER_NOTE)
                    .let { RenderingElementImpl(it) }
            Duration.HALF -> GlyphFactory.getRest(NoteType.HALF_NOTE)
                    .let { RenderingElementImpl(it) }
            else -> throw IllegalArgumentException("Unhandled rest duration: ${duration}")
        }

        noteRenderedElement.xPosition = xPosition
        noteRenderedElement.yPosition = yPosition

        return noteRenderedElement


    }


}