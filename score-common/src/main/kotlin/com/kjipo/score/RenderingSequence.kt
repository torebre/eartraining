package com.kjipo.score


import com.kjipo.svg.GlyphData
import kotlinx.serialization.*


@Serializable
data class RenderingSequence(val renderGroups: List<RenderGroup>, val viewBox: ViewBox, val definitions: Map<String, GlyphData>)

@Serializable
data class ViewBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int)

@Serializable
class RenderGroup { //(val renderingElements: List<PositionedRenderingElement>, val transform: Translation? = null, val renderGroup: RenderGroup? = null) {
    val renderingElements: MutableList<PositionedRenderingElement>
    val renderGroup: RenderGroup?
    val transform: Translation?

    constructor(renderingElements: List<PositionedRenderingElement>, transform: Translation? = null, renderGroup: RenderGroup? = null) {
        this.renderingElements = renderingElements.toMutableList()
        this.transform = transform
        this.renderGroup = renderGroup
        renderingElements.forEach { it.renderGroup = this }
    }

}
