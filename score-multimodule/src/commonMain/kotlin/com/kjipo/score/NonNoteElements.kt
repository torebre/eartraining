package com.kjipo.score

import com.kjipo.svg.*


class ClefElement(val clef: Clef, private val xPosition: Int, private val yPosition: Int, val id: String) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val positionedRenderingElement = PositionedRenderingElement.create(getGlyphData(), id)
        positionedRenderingElement.typeId = clef.name
        return listOf(positionedRenderingElement)
    }

    fun getGlyphData(): GlyphData {
        return when (clef) {
            Clef.G -> getGlyph("clefs.G")
            Clef.NONE -> blankGlyph
        }
    }
}


class TimeSignatureElement(private val nominator: Int, private val denominator: Int,
                           private val xPosition: Int, private val yPosition: Int, val id: String) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val nominatorGlyph = getNumberGlyph(nominator)
        val denominatorGlyph = getNumberGlyph(denominator)
        val pathElements = mutableListOf<PathElement>()

        pathElements.addAll(translateGlyph(nominatorGlyph, xPosition, yPosition).pathElements)
        pathElements.addAll(translateGlyph(translateGlyph(denominatorGlyph, xPosition, yPosition), 0, 50).pathElements)

        return listOf(PositionedRenderingElement.create(GlyphData("time_signature", pathElements, findBoundingBox(pathElements)), id))
    }

}