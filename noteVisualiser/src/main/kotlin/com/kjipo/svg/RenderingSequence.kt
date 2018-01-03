package com.kjipo.svg


class RenderingSequence(val renderingElements: List<PositionedRenderingElement>, val viewBox: ViewBox)

data class ViewBox(val xMin: Int, val yMin: Int, val xMax: Int, val yMax: Int)