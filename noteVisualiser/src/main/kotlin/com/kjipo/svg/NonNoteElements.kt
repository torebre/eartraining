package com.kjipo.svg

import com.kjipo.font.*
import java.util.stream.Collectors
import java.util.stream.Stream

class ClefElement(val clef: Clef, override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {


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


class TimeSignatureElement(val nominator: Int, val denominator: Int, override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val nominatorGlyph = GlyphFactory.getNumberGlyph(nominator)
        val denominatorGlyph = GlyphFactory.getNumberGlyph(denominator)

        val pathElements = Stream.concat(translateGlyph(nominatorGlyph, xPosition, yPosition).pathElements.stream(),
                translateGlyph(translateGlyph(denominatorGlyph, xPosition, yPosition), 0, 50).pathElements.stream())
                .collect(Collectors.toList())

        return RenderingElementImpl(GlyphData("time_signature", pathElements, findBoundingBox(pathElements)))
    }

}