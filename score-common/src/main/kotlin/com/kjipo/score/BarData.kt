package com.kjipo.score

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


    private fun ticksInMeasure(): Int {
        // TODO Need to look at time signature defined in earlier bars
        return if (timeSignature.denominator == 0) {
            4
        } else {
            timeSignature.nominator * denominatorInTicks()
        }
    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return timeSignature.nominator.div(4).times(TICKS_PER_QUARTER_NOTE)
    }

    fun build(barXoffset: Int = 0, barYoffset: Int = 0): RenderingSequence {
        val clefElement = clef?.let { ClefElement(it, 0, 0, "clef") }

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
                    val elements = mutableListOf<PositionedRenderingElement>()
                    val renderingElement = scoreRenderingElement.toRenderingElement()

                    scoreRenderingElement.xPosition = 0

                    if (scoreRenderingElement is NoteElement) {
                        val noteRenderingElement = scoreRenderingElement.toRenderingElement()
                        addExtraBarLinesForGClef(scoreRenderingElement.note, scoreRenderingElement.octave,
                                0,
                                noteRenderingElement.boundingBox.xMin.toInt(),
                                noteRenderingElement.boundingBox.xMax.toInt())?.let {
                            elements.add(it.toRenderingElement())
                        }

                        if (scoreRenderingElement.requiresStem()) {
                            val stem = addStem(renderingElement.boundingBox)
                            val stemElement = PositionedRenderingElement(listOf(stem),
                                    findBoundingBox(stem.pathElements),
                                    "stem-${barNumber++}-${stemCounter++}",
                                    0, //it.xPosition,
                                    0) // it.yPosition)

//                        if (beamGroups.containsKey(it.beamGroup)) {
//                            beamGroups.get(it.beamGroup)?.add(stemElement)
//                        } else {
//                            beamGroups.put(it.beamGroup, mutableListOf(stemElement))
//                        }

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

                    returnList.add(RenderGroup(elements, Translation(xPosition, 0)))

                }
            }
        }

//        returnList.addAll(scoreRenderingElements.map { it.toRenderingElement() })

//        scoreRenderingElements.filter { it is NoteElement }
//                .map { addExtraBarLines(it as NoteElement) }
//                .filterNotNull()
//                .let { returnList.addAll(it.map { it.toRenderingElement() }) }

        returnList.add(RenderGroup(listOf(BarLines(barXoffset, barYoffset, "bar-line").toRenderingElement()), null))

        return RenderingSequence(returnList, determineViewBox(returnList.flatMap { it.renderingElements }), emptyMap())
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