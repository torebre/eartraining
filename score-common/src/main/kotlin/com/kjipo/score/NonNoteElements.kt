package com.kjipo.score

import com.kjipo.svg.*


class ClefElement(val clef: Clef, override var xPosition: Int, override var yPosition: Int, val id: String) : ScoreRenderingElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val positionedRenderingElement = PositionedRenderingElement.create(getGlyphData(), id)
        positionedRenderingElement.typeId = clef.name
        return positionedRenderingElement;
    }

    fun getGlyphData(): GlyphData {
        return when (clef) {
            Clef.G -> getGlyph("clefs.G")
            Clef.NONE -> blankGlyph
        }
    }
}


class TimeSignatureElement(val nominator: Int, val denominator: Int, override var xPosition: Int, override var yPosition: Int, val id: String) : ScoreRenderingElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val nominatorGlyph = getNumberGlyph(nominator)
        val denominatorGlyph = getNumberGlyph(denominator)
        val pathElements = mutableListOf<PathElement>()

        pathElements.addAll(translateGlyph(nominatorGlyph, xPosition, yPosition).pathElements)
        pathElements.addAll(translateGlyph(translateGlyph(denominatorGlyph, xPosition, yPosition), 0, 50).pathElements)

        return PositionedRenderingElement.create(GlyphData("time_signature", pathElements, findBoundingBox(pathElements)), id)
    }

}