package com.kjipo.score

abstract class ScoreRenderingElement(var xPosition: Int = 0,
                                     var yPosition: Int = 0,
                                     var renderGroup: RenderGroup? = null) {

    abstract fun toRenderingElement(): List<PositionedRenderingElement>
}