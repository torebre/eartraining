package com.kjipo.svg

import com.kjipo.font.GlyphFactory

class ClefElement(val clef: Clef, override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {
    override var tieGroup: Int = 0


    override fun toRenderingElement(): PositionedRenderingElement {
        val glyphData = when {
            clef == Clef.G -> GlyphFactory.getGlyph("clefs.G")
            clef == Clef.NONE -> GlyphFactory.blankGlyph
            else -> {
                GlyphFactory.blankGlyph
            }
        }

        return RenderingElementImpl(glyphData)
    }


}