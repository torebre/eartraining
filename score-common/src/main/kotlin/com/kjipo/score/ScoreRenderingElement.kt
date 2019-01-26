package com.kjipo.score

interface ScoreRenderingElement {
    var xPosition: Int
    var yPosition: Int

    fun toRenderingElement(): List<PositionedRenderingElement>
}