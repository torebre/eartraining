package com.kjipo.score

import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getRest


class RestElement(override var duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  override val id: String) : TemporalElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val glyphData = getRest(duration)
        return PositionedRenderingElement(listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox,
                id,
                xPosition,
                yPosition)
    }

}