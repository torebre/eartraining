package com.kjipo.svg

interface ScoreRenderingElement {
    var xPosition: Int
    var yPosition: Int

    fun toRenderingElement(): PositionedRenderingElement
}