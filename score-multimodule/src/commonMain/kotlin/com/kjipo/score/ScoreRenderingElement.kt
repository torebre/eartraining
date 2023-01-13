package com.kjipo.score

import com.kjipo.svg.GlyphData


/**
 * Subclasses of this can create rendering data.
 */
abstract class ScoreRenderingElement(
    open var translation: Translation? = null,
) : ScoreElementMarker {

    abstract fun toRenderingElement(): List<PositionedRenderingElementParent>

    open fun doLayout(pixelsPerTick: Double) {
        // Do nothing by default
    }

    open fun getGlyphs(): Map<String, GlyphData> = emptyMap()
}