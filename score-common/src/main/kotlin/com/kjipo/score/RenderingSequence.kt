package com.kjipo.score


import com.kjipo.svg.GlyphData
import kotlinx.serialization.*


@Serializable
data class RenderingSequence(val renderGroups: List<RenderGroup>, val viewBox: ViewBox, val definitions: Map<String, GlyphData>)

@Serializable
data class ViewBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int)

@Serializable
data class RenderGroup(val renderingElements: List<PositionedRenderingElement>, val transform: Translation? = null)