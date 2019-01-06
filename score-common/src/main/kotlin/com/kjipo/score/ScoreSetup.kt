package com.kjipo.score

import com.kjipo.svg.GlyphData
import com.kjipo.svg.findBoundingBox
import com.kjipo.svg.getGlyph
import kotlinx.serialization.json.JSON

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
        return noteElements.find { it.id == activeElement }?.let {
            noteElements.filter { temporalElement ->
                temporalElement.id == activeElement
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
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        val definitionMap = mutableMapOf<String, GlyphData>()

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach {
                    definitionMap[it.duration.name] = getGlyph(it.duration)
                }

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 0

        bars.forEach { barData ->
            val currentBar = barData.build(barXoffset, barYoffset)
            currentBar.definitions.forEach {
                if(!definitionMap.containsKey(it.key)) {
                    definitionMap[it.key] = it.value
                }
            }

            renderingSequences.add(currentBar)
            barXoffset += barXspace
            barYoffset += barYspace
        }

        val beamGroups = mutableMapOf<Int, MutableCollection<PositionedRenderingElement>>()

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

        renderingSequences.add(RenderingSequence(listOf(RenderGroup(renderingElements, null)), determineViewBox(renderingElements), definitionMap))

        return mergeRenderingSequences(renderingSequences)
    }


    private fun mergeRenderingSequences(renderingSequences: Collection<RenderingSequence>): RenderingSequence {
        val renderGroups = renderingSequences.flatMap { it.renderGroups }.toList()
        val definitions = mutableMapOf<String, GlyphData>()
        renderingSequences.flatMap { it.definitions.entries }.forEach {
            if (!definitions.containsKey(it.key)) {
                definitions.put(it.key, it.value)
            }
        }

        // TODO Viewbox will be wrong since translations are not taken into account
        return RenderingSequence(renderGroups, determineViewBox(renderGroups.flatMap { it.renderingElements }), definitions)
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
