package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.findBoundingBox
import com.kjipo.svg.getNoteHeadGlyph
import kotlin.math.ceil

class BarData(val context: Context, private val debug: Boolean = false) {
    var clef: Clef = Clef.NONE
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    var widthAvailableForTemporalElements = DEFAULT_BAR_WIDTH

    var timeSignature = TimeSignature(0, 0)


    fun build(barXoffset: Int = 0, barYoffset: Int = 0): RenderingSequence {
        val definitions = mutableMapOf<String, GlyphData>()

        val clefElement = getClefElement(barXoffset, barYoffset, definitions)

        val timeSignatureElement = if (timeSignature.nominator == 0) {
            null
        } else {
            TimeSignatureElement(timeSignature.nominator, timeSignature.denominator, timeSignatureXOffset, timeSignatureYOffset, "time")
        }

        widthAvailableForTemporalElements = getWidthAvailable(clefElement, timeSignatureElement)

        val valTotalTicksInBar = scoreRenderingElements.filterIsInstance<TemporalElement>()
                .map { it.duration.ticks }.sum()
        val pixelsPerTick = widthAvailableForTemporalElements.toDouble() / valTotalTicksInBar
        val xOffset = DEFAULT_BAR_WIDTH - widthAvailableForTemporalElements
        val returnList = mutableListOf<RenderGroup>()

        clefElement?.let { returnList.add(RenderGroup(clefElement.toRenderingElement(), null)) }
        timeSignatureElement?.let { returnList.add(RenderGroup(timeSignatureElement.toRenderingElement(), null)) }

        var tickCounter = 0

        for (scoreRenderingElement in scoreRenderingElements) {

            when (scoreRenderingElement) {
                is TemporalElement -> {
                    val xPosition = barXoffset + ceil(xOffset.plus(tickCounter.times(pixelsPerTick))).toInt()
                    var yPosition = barYoffset
                    val elements = mutableListOf<PositionedRenderingElement>()

                    scoreRenderingElement.xPosition = 0

                    if (scoreRenderingElement is NoteElement) {
                        yPosition += calculateVerticalOffset(scoreRenderingElement.note, scoreRenderingElement.octave)

                        if (scoreRenderingElement.requiresStem()) {
                            scoreRenderingElement.stem = context.stemUp(scoreRenderingElement.id)

                            addStem(scoreRenderingElement, definitions)
                        }
                    }
                    else if(scoreRenderingElement is NoteGroupElement) {
                        yPosition += scoreRenderingElement.yPosition

//                        if (scoreRenderingElement.requiresStem()) {
//                            // TODO Update for note group handling
//                            scoreRenderingElement.stem = scoreState.stemUp(scoreRenderingElement.id)
//
//                            if (scoreRenderingElement.stem == Stem.UP) {
//                                // Use the bounding box for the note head of a half note to determine
//                                // how far to move the stem so that it is on the right side of the note head
//                                val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
//                                definitions[Stem.UP.name] = GlyphData(Stem.UP.name, stem.pathElements, findBoundingBox(stem.pathElements))
//                            } else if (scoreRenderingElement.stem == Stem.DOWN) {
//                                val stem = addStem(BoundingBox(0.0, 0.0, 2.0, 0.0), false)
//                                definitions[Stem.DOWN.name] = GlyphData(Stem.DOWN.name, stem.pathElements, findBoundingBox(stem.pathElements))
//                            }
//                        }
                    }
                    tickCounter += scoreRenderingElement.duration.ticks

                    if (debug) {
                        val width = barXoffset.plus(ceil(xOffset.plus(tickCounter.times(pixelsPerTick)))).minus(scoreRenderingElement.xPosition).toInt()
                        val debugBox = Box(scoreRenderingElement.xPosition, scoreRenderingElement.yPosition, width, scoreRenderingElement.yPosition, "debug")
                        returnList.add(RenderGroup(debugBox.toRenderingElement(), null))
                    }


                    val renderingElement = scoreRenderingElement.toRenderingElement()
                    elements.addAll(renderingElement)

                    val renderGroup = RenderGroup(elements, Translation(xPosition, yPosition))

                    // TODO This is confusing. Try to fit in render groups differently
                    scoreRenderingElement.renderGroup = renderGroup

                    returnList.add(renderGroup)
                }
            }

            definitions.putAll(scoreRenderingElement.getGlyphs())
        }

        returnList.add(RenderGroup(BarLines(barXoffset, barYoffset, "bar-line").toRenderingElement(), null))

        return RenderingSequence(returnList, determineViewBox(returnList.flatMap { it.renderingElements }), definitions)
    }

    private fun addStem(
        scoreRenderingElement: NoteElement,
        definitions: MutableMap<String, GlyphData>
    ) {
        if (scoreRenderingElement.stem == Stem.UP) {
            // Use the bounding box for the note head of a half note to determine
            // how far to move the stem so that it is on the right side of the note head
            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
            definitions[Stem.UP.name] = GlyphData(Stem.UP.name, stem.pathElements, findBoundingBox(stem.pathElements))
        } else if (scoreRenderingElement.stem == Stem.DOWN) {
            val stem = addStem(BoundingBox(0.0, 0.0, 2.0, 0.0), false)
            definitions[Stem.DOWN.name] =
                GlyphData(Stem.DOWN.name, stem.pathElements, findBoundingBox(stem.pathElements))
        }
    }

    private fun getClefElement(barXoffset: Int, barYoffset: Int, definitions: MutableMap<String, GlyphData>): ClefElement? {
        return if (clef == Clef.NONE) {
            null
        } else {
            val element = ClefElement(clef, barXoffset, barYoffset, "clef")
            definitions[clef.name] = element.getGlyphData()
            element
        }
    }

    private fun getWidthAvailable(clefElement: ClefElement?, timeSignatureElement: TimeSignatureElement?): Int {
        return DEFAULT_BAR_WIDTH
                .minus(clefElement?.let {
                    val renderingElement = it.toRenderingElement()
                    renderingElement[0].boundingBox.xMax.minus(renderingElement[0].boundingBox.xMin).toInt()
                } ?: 0)
                .minus(timeSignatureElement?.let {
                    val renderingElement = it.toRenderingElement()
                    renderingElement[0].boundingBox.xMax.minus(renderingElement[0].boundingBox.xMin).toInt()
                } ?: 0)
                .minus(START_NOTE_ELEMENT_MARGIN)
    }

    override fun toString(): String {
        return "BarData(scoreRenderingElements=$scoreRenderingElements)"
    }


    companion object {
        var barNumber = 0
        var stemCounter = 0
    }


}