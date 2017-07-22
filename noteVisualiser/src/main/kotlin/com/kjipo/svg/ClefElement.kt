package com.kjipo.svg

import com.kjipo.font.GlyphFactory

class ClefElement(val clef: Clef, override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {


    override fun toRenderingElement(): PositionedRenderingElement {
         val glyphData = when {
            clef == Clef.G -> GlyphFactory.getGlyph("clefs.G")
            clef == Clef.NONE -> GlyphFactory.blankGlyph
            else -> {
                GlyphFactory.blankGlyph
            }
        }

        // TODO Make better method signature
        return RenderingElementImpl(listOf(glyphData), glyphData.boundingBox)
    }


}