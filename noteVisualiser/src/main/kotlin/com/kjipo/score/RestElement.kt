package com.kjipo.score


import com.kjipo.svg.GlyphFactory

class RestElement(override val duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int): TemporalElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val noteRenderedElement = RenderingElementImpl(GlyphFactory.getRest(duration))

        noteRenderedElement.xPosition = xPosition
        noteRenderedElement.yPosition = yPosition

        return noteRenderedElement
    }

}