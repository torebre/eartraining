package com.kjipo.score


import com.kjipo.svg.GlyphData
import kotlinx.serialization.*


@Serializable
data class RenderingSequence(
    val renderGroups: List<PositionedRenderingElementParent>,
    // TODO Fix it so that view boxes are computed for all kinds of rendering elements
    val viewBox: ViewBox?,
    val definitions: Map<String, GlyphData>
)

@Serializable
data class RenderingSequenceWithMetaData(
    val renderingSequence: RenderingSequence,
    val highlightElementsMap: Map<String, Collection<String>>
)

@Serializable
data class ViewBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int)

@Serializable
class RenderGroup {
    val renderingElements: MutableList<PositionedRenderingElementParent>
    val renderGroup: RenderGroup?
    val transform: Translation?

    constructor(
        renderingElements: List<PositionedRenderingElementParent>,
        transform: Translation? = null,
        renderGroup: RenderGroup? = null
    ) {
        this.renderingElements = renderingElements.toMutableList()
        this.transform = transform
        this.renderGroup = renderGroup
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as RenderGroup

        if (renderingElements != other.renderingElements) return false
        if (renderGroup != other.renderGroup) return false
        if (transform != other.transform) return false

        return true
    }

    override fun hashCode(): Int {
        var result = renderingElements.hashCode()
        result = 31 * result + (renderGroup?.hashCode() ?: 0)
        result = 31 * result + (transform?.hashCode() ?: 0)
        return result
    }


}
