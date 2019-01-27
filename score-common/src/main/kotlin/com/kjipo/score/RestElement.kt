package com.kjipo.score

import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getRest


class RestElement(override var duration: Duration,
                  override val id: String) : ScoreRenderingElement(0, 0), TemporalElement {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val glyphData = getRest(duration)
        return listOf(PositionedRenderingElement(listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox,
                id))
    }

}