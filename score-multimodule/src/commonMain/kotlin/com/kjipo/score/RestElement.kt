package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getRestGlyph


class RestElement(
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter(),
    override val properties: Map<String, String> = mapOf()
) : ScoreRenderingElement(0, 0), TemporalElement, HighlightableElement {

    private val typeName = "rest_${duration.name}"

    private val highlightElements = mutableSetOf<String>()

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val glyphData = getRestGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement(
            listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
            glyphData.boundingBox,
            id
        ).apply {
            yTranslate = -30
            typeId = typeName
        }
        highlightElements.add(positionedRenderingElement.id)
        return listOf(positionedRenderingElement)
    }

    override fun getGlyphs(): Map<String, GlyphData> = mapOf(Pair(typeName, getRestGlyph(duration)))

    override fun getIdsOfHighlightElements() = highlightElements

    override fun toString(): String {
        return "RestElement(duration=$duration, id='$id')"
    }

}