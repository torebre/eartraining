package com.kjipo.score

import com.kjipo.svg.getRest


class RestElement(override val duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int): TemporalElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val noteRenderedElement = RenderingElementImpl(getRest(duration))

        noteRenderedElement.xPosition = xPosition
        noteRenderedElement.yPosition = yPosition

        return noteRenderedElement
    }

}