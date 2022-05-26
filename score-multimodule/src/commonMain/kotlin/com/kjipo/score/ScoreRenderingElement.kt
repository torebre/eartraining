package com.kjipo.score

import com.kjipo.svg.GlyphData


/**
 * Subclasses of this can create rendering data.
 */
abstract class ScoreRenderingElement(
    var translation: Translation? = null
) {

    abstract fun toRenderingElement(): List<PositionedRenderingElementParent>

    open fun getGlyphs(): Map<String, GlyphData> = emptyMap()
}