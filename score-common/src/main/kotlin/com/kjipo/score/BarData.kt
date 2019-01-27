package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.findBoundingBox
import com.kjipo.svg.getGlyph
import kotlin.math.ceil

class BarData(val debug: Boolean = false) {
    var clef: Clef? = null
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()
    var previousBar: BarData? = null


    var widthAvailableForTemporalElements = DEFAULT_BAR_WIDTH

    var timeSignature = TimeSignature(0, 0)

    val timeSignatureXOffset = 80
    val timeSignatureYOffset = -25


    fun build(barXoffset: Int = 0, barYoffset: Int = 0): RenderingSequence {
        val definitions = mutableMapOf<String, GlyphData>()

        val clefElement = clef?.let {
            val element = ClefElement(it, 0, 0, "clef")
            definitions[it.name] = element.getGlyphData()
            element
        }

        val timeSignatureElement = if (timeSignature.nominator == 0) {
            null
        } else {
            TimeSignatureElement(timeSignature.nominator, timeSignature.denominator, timeSignatureXOffset, timeSignatureYOffset, "time")
        }

        widthAvailableForTemporalElements = getWidthAvailable(clefElement, timeSignatureElement)

        val valTotalTicksInBar = scoreRenderingElements.filter { it is TemporalElement }
                .map { (it as TemporalElement).duration.ticks }.sum()
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
                    var yPosition = 0
                    val elements = mutableListOf<PositionedRenderingElement>()
                    val renderingElement = scoreRenderingElement.toRenderingElement()

                    scoreRenderingElement.xPosition = 0

                    if (scoreRenderingElement is NoteElement) {
                        yPosition = calculateVerticalOffset(scoreRenderingElement.note, scoreRenderingElement.octave)

                        if (scoreRenderingElement.requiresStem()) {
                            // TODO Determine whether the stem should go up or down

                            // Use the bounding box for the note head of a half note to determine
                            // how far to move the stem so that it is on the right side of the note head
                            val stem = addStem(getGlyph(Duration.HALF).boundingBox)
                            definitions[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
                        }
                    }

                    scoreRenderingElement.yPosition += barYoffset
                    tickCounter += scoreRenderingElement.duration.ticks

                    if (debug) {
                        val width = barXoffset.plus(ceil(xOffset.plus(tickCounter.times(pixelsPerTick)))).minus(scoreRenderingElement.xPosition).toInt()
                        val debugBox = Box(scoreRenderingElement.xPosition, scoreRenderingElement.yPosition, width, scoreRenderingElement.yPosition, "debug")
                        returnList.add(RenderGroup(debugBox.toRenderingElement(), null))
                    }

                    elements.addAll(renderingElement)

                    val renderGroup = RenderGroup(elements, Translation(xPosition, yPosition))

                    // TODO This is confusing. Try to fit in render groups differently
                    scoreRenderingElement.renderGroup = renderGroup
//                    elements.forEach { it.renderGroup = renderGroup }

                    returnList.add(renderGroup)


                }
            }
        }

        returnList.add(RenderGroup(BarLines(barXoffset, barYoffset, "bar-line").toRenderingElement(), null))

        return RenderingSequence(returnList, determineViewBox(returnList.flatMap { it.renderingElements }), definitions)
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


    companion object {
        var barNumber = 0
        var stemCounter = 0
    }


}