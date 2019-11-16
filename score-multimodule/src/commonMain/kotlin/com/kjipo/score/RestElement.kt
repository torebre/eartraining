package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getRestGlyph


class RestElement(override var duration: Duration,
                  override val id: String = "rest_${restIdCounter++}") : ScoreRenderingElement(0, 0), TemporalElement {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val glyphData = getRestGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement(listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox,
                id)
        positionedRenderingElement.yTranslate = -30
        positionedRenderingElement.typeId = "rest_${duration.name}"
        return listOf(positionedRenderingElement)
    }

    override fun getGlyphs(): Map<String, GlyphData> = mapOf(Pair("rest_${duration.name}", getRestGlyph(duration)))

    companion object {
        var restIdCounter = 0
    }

}