package com.kjipo.score

import com.kjipo.svg.GlyphData


/**
 * Subclasses of this can create rendering data.
 */
abstract class ScoreRenderingElement(var xPosition: Int = 0,
                                     var yPosition: Int = 0,
                                     var translation: Translation? = null
) {


    abstract fun toRenderingElement(): List<PositionedRenderingElement>

    open fun getGlyphs(): Map<String, GlyphData> = emptyMap()
}