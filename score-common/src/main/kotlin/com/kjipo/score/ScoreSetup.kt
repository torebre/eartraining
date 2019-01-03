package com.kjipo.score

import com.kjipo.svg.findBoundingBox
import kotlinx.serialization.json.JSON
import kotlin.math.abs
import kotlin.math.roundToInt

class ScoreSetup {
    val noteElements = mutableListOf<TemporalElement>()
    val bars = mutableListOf<BarData>()

    val test = mutableListOf<Int>()

    fun findNote(elementId: String): NoteElement? {
        return noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .find { it.id == elementId }
    }

    fun moveNoteOneStep(id: String, up: Boolean) {
        findNote(id)?.let {
            if (up) {
                if (it.note == NoteType.H) {
                    it.note = NoteType.C
                    ++it.octave
                } else {
                    it.note = NoteType.values()[(it.note.ordinal + 1) % NoteType.values().size]
                }
            } else {
                if (it.note == NoteType.C) {
                    it.note = NoteType.H
                    --it.octave
                } else {
                    it.note = NoteType.values()[(NoteType.values().size + it.note.ordinal - 1) % NoteType.values().size]
                }
            }
        }
    }

    fun getScoreAsJson(): String {
        return JSON.stringify(RenderingSequence.serializer(), build())
    }

    fun getIdOfFirstSelectableElement() = noteElements.map { it.id }.firstOrNull()

    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String? {
        return noteElements.find { it.id.equals(activeElement) }?.let { noteElement ->
            noteElements.filter { temporalElement ->
                temporalElement.id.equals(activeElement)
            }.map { it ->
                val index = noteElements.indexOf(it)
                if (lookLeft) {
                    if (index == 0) {
                        0
                    } else {
                        index - 1
                    }
                } else {
                    if (index == noteElements.lastIndex) {
                        noteElements.lastIndex
                    } else {
                        index + 1
                    }
                }
            }
                    .map { noteElements[it].id }.firstOrNull()
        }
    }

    fun updateDuration(id: String, keyPressed: Int) {
        noteElements.find {
            it.id.equals(id)
        }?.let {
            when (keyPressed) {
                1 -> it.duration = Duration.QUARTER
                2 -> it.duration = Duration.HALF
                3 -> it.duration = Duration.WHOLE
            }
        }
    }

    fun build(): RenderingSequence {
        var stemCounter = 0

        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingElements = mutableListOf<PositionedRenderingElement>()

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach {
                    it.yPosition = calculateVerticalOffset(it.note, it.octave)
                }

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 0

        bars.forEach {
            renderingElements.addAll(it.build(barXoffset, barYoffset))
            barXoffset += barXspace
            barYoffset += barYspace
        }

        val beamGroups = mutableMapOf<Int, MutableCollection<PositionedRenderingElement>>()

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach {
                    if (it.requiresStem()) {
                        val stem = addStem(it.toRenderingElement().boundingBox)
                        val stemElement = PositionedRenderingElement(listOf(stem),
                                findBoundingBox(stem.pathElements),
                                "stem-${stemCounter++}",
                                it.xPosition,
                                it.yPosition)

                        if (beamGroups.containsKey(it.beamGroup)) {
                            beamGroups.get(it.beamGroup)?.add(stemElement)
                        } else {
                            beamGroups.put(it.beamGroup, mutableListOf(stemElement))
                        }
                    }
                }

        beamGroups.forEach {
            if (it.key == 0) {
                renderingElements.addAll(it.value)
            } else {
                renderingElements.addAll(it.value)
                renderingElements.add(handleBeams(it.value))
            }
        }

        var tieElementCounter = 0

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .filter { it.tie != null }
                .forEach { from ->
                    findNote(from.tie!!)?.let { to ->
                        val tieElement = TieElement("tie-element-$tieElementCounter", from.xPosition, from.yPosition, to.xPosition.toDouble(), to.yPosition.toDouble())
                        ++tieElementCounter
                        renderingElements.add(tieElement.toRenderingElement())
                    }
                }

        return RenderingSequence(renderingElements, determineViewBox(renderingElements))
    }

    private fun determineViewBox(renderingElements: Collection<PositionedRenderingElement>): ViewBox {
        var xMin = 0.0
        var yMin = 0.0
        var xMax = 0.0
        var yMax = 0.0
        renderingElements.forEach {
            if (xMin > it.boundingBox.xMin) {
                xMin = it.boundingBox.xMin
            }
            if (xMax < it.boundingBox.xMax) {
                xMax = it.boundingBox.xMax
            }
            if (yMin > it.boundingBox.yMin) {
                yMin = it.boundingBox.yMin
            }
            if (yMax < it.boundingBox.yMax) {
                yMax = it.boundingBox.yMax
            }
        }

        xMin -= LEFT_MARGIN
        yMin -= TOP_MARGIN
        xMax += RIGHT_MARGIN
        yMax += BOTTOM_MARGIN

        // This is to try to get to cropping when xMin and/or yMin are less than 0
        if (xMin < 0) {
            xMax += abs(xMin)
        }
        if (yMin < 0) {
            yMax += abs(yMin)
        }

        return ViewBox(xMin.roundToInt(),
                yMin.roundToInt(),
                xMax.roundToInt(),
                yMax.roundToInt())
    }

    private fun handleBeams(beamGroup: Collection<PositionedRenderingElement>): PositionedRenderingElement {
        var beamCounter = 0
        val stemMinimum = beamGroup.reduce({ s, t ->
            s.let {
                if (it.xPosition < t.xPosition) {
                    it
                } else {
                    t
                }
            }
        })

        val stemMaximum = beamGroup.reduce({ s, t ->
            s.let {
                if (it.xPosition > t.xPosition) {
                    it
                } else {
                    t
                }
            }
        })

        val height = stemMinimum.boundingBox.height()
        val yStart = minOf(stemMinimum.yPosition.toDouble() + height, stemMaximum.yPosition.toDouble() + height)
        val yEnd = maxOf(stemMinimum.yPosition.toDouble() + height, stemMaximum.yPosition.toDouble() + height)
        val boundingBoxHeight = yEnd - yStart

        val beamElement = addBeam(0, 0,
                stemMaximum.xPosition.minus(stemMinimum.xPosition),
                -boundingBoxHeight.toInt(),
                // TODO This probably does not work now
                stemMinimum.boundingBox.width().toInt(),
                -height.toInt()
        )

        return PositionedRenderingElement(listOf(beamElement),
                findBoundingBox(beamElement.pathElements),
                "beam-${beamCounter++}",
                stemMinimum.xPosition,
                stemMinimum.yPosition)
    }

}





