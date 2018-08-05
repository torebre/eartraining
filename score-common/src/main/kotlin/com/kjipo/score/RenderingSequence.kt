package com.kjipo.score


import kotlinx.serialization.*


@Serializable
class RenderingSequence(val renderingElements: List<PositionedRenderingElement>,
                        val viewBox: ViewBox)

@Serializable
class ViewBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int)