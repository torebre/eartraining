package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getRestGlyph


class RestElement(
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter()
) : ScoreRenderingElement(0, 0), TemporalElement, HighlightableElement {

    private val highlightElements = mutableSetOf<String>()

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val glyphData = getRestGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement(
            listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
            glyphData.boundingBox,
            id
        )
        positionedRenderingElement.yTranslate = -30
        positionedRenderingElement.typeId = context.getAndIncrementIdCounter()
        return listOf(positionedRenderingElement)
    }

    override fun getGlyphs(): Map<String, GlyphData> = mapOf(Pair("rest_${duration.name}", getRestGlyph(duration)))

    override fun getIdsOfHighlightElements() = highlightElements

}