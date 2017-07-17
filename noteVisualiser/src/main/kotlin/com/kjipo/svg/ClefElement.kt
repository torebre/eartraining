package com.kjipo.svg

import com.kjipo.font.GlyphFactory

class ClefElement(val clef: Clef, override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {

    override fun toRenderingElement(): RenderingElement {
        return when {
            clef == Clef.G -> RenderingElementImpl(listOf(GlyphFactory.getGlyph("clefs.G")))
            clef == Clef.NONE -> RenderingElementImpl(emptyList())
            else -> {
                RenderingElementImpl(emptyList())
            }
        }
    }


}