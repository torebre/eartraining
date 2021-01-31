package com.kjipo.score

import com.kjipo.svg.GlyphData

abstract class ScoreRenderingElement(var xPosition: Int = 0,
                                     var yPosition: Int = 0,
                                     var renderGroup: RenderGroup? = null) {

    var translation: Translation? = null

    abstract fun toRenderingElement(): List<PositionedRenderingElement>

    open fun getGlyphs(): Map<String, GlyphData> = emptyMap()
}