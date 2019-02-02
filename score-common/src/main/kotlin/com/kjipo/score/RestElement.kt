package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getRest


class RestElement(override var duration: Duration,
                  override val id: String) : ScoreRenderingElement(0, 0), TemporalElement {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val glyphData = getRest(duration)
        val positionedRenderingElement = PositionedRenderingElement(listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox,
                id)
        positionedRenderingElement.yTranslate = -30
        positionedRenderingElement.typeId = "rest_${duration.name}"
        return listOf(positionedRenderingElement)
    }

    override fun getGlyphs(): Map<String, GlyphData> = mapOf(Pair("rest_${duration.name}", getRest(duration)))

}