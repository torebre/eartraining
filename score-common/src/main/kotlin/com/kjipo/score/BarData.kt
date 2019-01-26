package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.findBoundingBox
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

        clefElement?.let { returnList.add(RenderGroup(listOf(clefElement.toRenderingElement()), null)) }
        timeSignatureElement?.let { returnList.add(RenderGroup(listOf(timeSignatureElement.toRenderingElement()), null)) }

        var tickCounter = 0
        var stemCounter = 0

        for (scoreRenderingElement in scoreRenderingElements) {
            when (scoreRenderingElement) {
                is TemporalElement -> {
                    val xPosition = barXoffset + ceil(xOffset.plus(tickCounter.times(pixelsPerTick))).toInt()
                    var yPosition = 0
                    val elements = mutableListOf<PositionedRenderingElement>()
                    val renderingElement = scoreRenderingElement.toRenderingElement()

                    scoreRenderingElement.xPosition = 0

                    if (scoreRenderingElement is NoteElement) {
                        val noteRenderingElement = scoreRenderingElement.toRenderingElement()
                        yPosition = calculateVerticalOffset(scoreRenderingElement.note, scoreRenderingElement.octave)
                        addExtraBarLinesForGClef(scoreRenderingElement.note, scoreRenderingElement.octave,
                                0,
                                -yPosition,
                                noteRenderingElement.boundingBox.xMin.toInt(),
                                noteRenderingElement.boundingBox.xMax.toInt())?.let {
                            elements.add(it.toRenderingElement())
                        }

                        if (scoreRenderingElement.requiresStem()) {
                            // TODO Determine whether the stem should go up or down
                            val stem = addStem(renderingElement.boundingBox)

                            val stemElement = PositionedRenderingElement(listOf(stem),
                                    findBoundingBox(stem.pathElements),
                                    "stem-${barNumber++}-${stemCounter++}",
                                    0,
                                    0)
                            stemElement.typeId = STEM_UP

                            definitions[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))

                            elements.add(stemElement)
                        }
                    }

                    scoreRenderingElement.yPosition += barYoffset
                    tickCounter += scoreRenderingElement.duration.ticks

                    if (debug) {
                        val width = barXoffset.plus(ceil(xOffset.plus(tickCounter.times(pixelsPerTick)))).minus(scoreRenderingElement.xPosition).toInt()
                        val debugBox = Box(scoreRenderingElement.xPosition, scoreRenderingElement.yPosition, width, scoreRenderingElement.yPosition, "debug")
                        returnList.add(RenderGroup(listOf(debugBox.toRenderingElement()), null))
                    }

                    elements.add(renderingElement)
                    returnList.add(RenderGroup(elements, Translation(xPosition, yPosition)))
                }
            }
        }

        returnList.add(RenderGroup(listOf(BarLines(barXoffset, barYoffset, "bar-line").toRenderingElement()), null))

        return RenderingSequence(returnList, determineViewBox(returnList.flatMap { it.renderingElements }), definitions)
    }

    private fun getWidthAvailable(clefElement: ClefElement?, timeSignatureElement: TimeSignatureElement?): Int {
        return DEFAULT_BAR_WIDTH
                .minus(clefElement?.let {
                    val renderingElement = it.toRenderingElement()
                    renderingElement.boundingBox.xMax.minus(renderingElement.boundingBox.xMin).toInt()
                } ?: 0)
                .minus(timeSignatureElement?.let {
                    val renderingElement = it.toRenderingElement()
                    renderingElement.boundingBox.xMax.minus(renderingElement.boundingBox.xMin).toInt()
                } ?: 0)
                .minus(START_NOTE_ELEMENT_MARGIN)
    }


    companion object {
        var barNumber = 0
    }


}