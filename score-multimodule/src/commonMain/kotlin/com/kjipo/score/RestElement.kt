package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.getRestGlyph


class RestElement(
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter(),
    override val properties: Map<String, String> = mapOf()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {

    private val typeName = "rest_${duration.name}"

    private val highlightElements = mutableSetOf<String>()

    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        val glyphData = getRestGlyph(duration)
        val positionedRenderingElement = TranslatedRenderingElementUsingReference(
            id,
            null,
            (translation ?: Translation(0.0, 0.0)).let {
                Translation(it.xShift, it.yShift - 30)
            },
            typeName,
            true,
            glyphData.boundingBox
        )
        highlightElements.add(positionedRenderingElement.id)
        return listOf(positionedRenderingElement)
    }

    override fun getGlyphs(): Map<String, GlyphData> = mapOf(Pair(typeName, getRestGlyph(duration)))

    override fun getIdsOfHighlightElements() = highlightElements

    override fun toString(): String {
        return "RestElement(duration=$duration, id='$id')"
    }

}