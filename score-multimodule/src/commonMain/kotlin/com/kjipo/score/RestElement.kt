package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.getRestGlyph


class RestElement(
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter(),
    private val properties: ElementWithProperties = Properties()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement, ElementWithProperties by properties {

    private val typeName = "rest_${duration.name}"
    private var positionedRenderingElement: PositionedRenderingElementParent? = null
    private var internalShiftX = 0.0

    private val highlightElements = mutableSetOf<String>()


    override fun doLayout(pixelsPerTick: Double) {
        doLayoutInternal()
        val debugBox = determineDebugBox(
            "${id}_debug_box",
            positionedRenderingElement.let {
                if (it == null) {
                    emptyList()
                } else {
                    listOf(it)
                }
            }
        )

        internalShiftX = (duration.ticks * pixelsPerTick - debugBox.width) / 2.0
        doLayoutInternal()
    }

    private fun doLayoutInternal() {
        highlightElements.clear()
        positionedRenderingElement = TranslatedRenderingElementUsingReference(
            id,
            null,
            getTranslation().let {
                Translation(it.xShift, it.yShift - 22.5)
            },
            typeName,
            true,
            getRestGlyph(duration).boundingBox
        ).also {
            highlightElements.add(it.id)
        }
    }

    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        return positionedRenderingElement.let {
            if (it == null) {
                emptyList()
            } else {
                listOf(it)
            }
        }
    }

    private fun getTranslationX() = (translation?.xShift ?: 0.0) + internalShiftX

    private fun getTranslationY() = (translation?.yShift ?: 0.0)

    private fun getTranslation() = Translation(getTranslationX(), getTranslationY())
    override fun getGlyphs(): Map<String, GlyphData> = mapOf(Pair(typeName, getRestGlyph(duration)))

    override fun getIdsOfHighlightElements() = highlightElements

    override fun toString(): String {
        return "RestElement(duration=$duration, id='$id')"
    }

}