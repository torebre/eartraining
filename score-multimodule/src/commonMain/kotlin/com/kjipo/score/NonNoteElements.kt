package com.kjipo.score

import com.kjipo.svg.*


class ClefElement(val clef: Clef, val id: String) :
    ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        return getGlyphData().let { glyphData ->
            listOf(
                PositionedRenderingElement.create(
                    glyphData.boundingBox,
                    id,
                    Translation(0.0, 0.0),
                    clef.name,
                    true
                )
            )
        }
    }

    fun getGlyphData(): GlyphData {
        return when (clef) {
            Clef.G -> getGlyph("clefs.G")
            Clef.NONE -> blankGlyph
        }
    }
}


class TimeSignatureElement(
    private val nominator: Int, private val denominator: Int,
    private val xPosition: Double, private val yPosition: Double, val id: String
) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val nominatorGlyph = getNumberGlyph(nominator)
        val denominatorGlyph = getNumberGlyph(denominator)
        val pathElements = mutableListOf<PathElement>()

        pathElements.addAll(translateGlyph(nominatorGlyph, xPosition, yPosition).pathElements)
        pathElements.addAll(translateGlyph(translateGlyph(denominatorGlyph, xPosition, yPosition), 0.0, 50.0).pathElements)

        return listOf(
            PositionedRenderingElement.create(
                GlyphData(
                    "time_signature",
                    pathElements,
                    findBoundingBox(pathElements)
                ), id
            )
        )
    }

}