package com.kjipo.score


import kotlinx.serialization.*


@Serializable
data class RenderingSequence(val renderingElements: List<PositionedRenderingElement>,
                        val viewBox: ViewBox)

@Serializable
data class ViewBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int)