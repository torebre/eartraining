package com.kjipo.score

import com.kjipo.svg.GlyphData
import kotlinx.serialization.json.JSON

class ScoreSetup {
    val noteElements = mutableListOf<TemporalElement>()
    val bars = mutableListOf<BarData>()
    val ties = mutableListOf<TiePair>()
    val beams = mutableListOf<BeamGroup>()

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

                    // TODO Can be more efficient
                    definitionMap.putAll(it.getGlyphs())

                }

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 0

        bars.forEach { barData ->
            val currentBar = barData.build(barXoffset, barYoffset)
            currentBar.definitions.forEach {
                if (!definitionMap.containsKey(it.key)) {
                    definitionMap[it.key] = it.value
                }
            }

            renderingSequences.add(currentBar)
            barXoffset += barXspace
            barYoffset += barYspace
        }

        var beamCounter = 0

        beams.map { beam ->
            val firstNote = beam.noteElements.first()
            val lastNote = beam.noteElements.last()

            val firstStem = firstNote.getStem()
            val lastStem = lastNote.getStem()

            var startX = 0.0
            var startY = 0.0

            firstNote.renderGroup?.let { renderGroup ->
                renderGroup.transform?.let {
                    startX = firstStem.boundingBox.xMax + it.xShift
                    startY = firstStem.boundingBox.yMin + it.yShift
                }
            }

            var stopX = 0.0
            var stopY = 0.0
            lastNote.renderGroup?.let { renderGroup ->
                renderGroup.transform?.let {
                    stopX = lastStem.boundingBox.xMax + it.xShift
                    stopY = lastStem.boundingBox.yMin + it.yShift
                }
            }

            val beamElement = BeamElement("beam-${beamCounter++}",
                    Pair(firstStem.boundingBox.xMax, firstStem.boundingBox.yMin),
                    Pair(stopX - startX, stopY - startY), firstNote.renderGroup)

            firstNote.renderGroup?.let {
                it.renderingElements.addAll(beamElement.toRenderingElement())
            }
        }


        for ((tieElementCounter, tie) in ties.withIndex()) {
            val firstStem = tie.startNote.getStem()
            val lastStem = tie.endNote.getStem()

            var startX = 0.0
            var startY = 0.0

            tie.startNote.renderGroup?.let { renderGroup ->
                renderGroup.transform?.let {
                    startX = firstStem.boundingBox.xMax + it.xShift
                    startY = firstStem.boundingBox.yMin + it.yShift
                }
            }

            var stopX = 0.0
            var stopY = 0.0
            tie.endNote.renderGroup?.let { renderGroup ->
                renderGroup.transform?.let {
                    stopX = lastStem.boundingBox.xMax + it.xShift
                    stopY = lastStem.boundingBox.yMin + it.yShift
                }
            }

            val tieElement = TieElement("tie-element-$tieElementCounter", stopX, stopY)

            tieElement.xPosition = startX.toInt()
            tieElement.yPosition = startY.toInt()

            renderingElements.addAll(tieElement.toRenderingElement())
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


    fun switchBetweenNoteAndRest(id: String): String {
        noteElements.find { it.id == id }?.let { temporalElement ->
            when (temporalElement) {
                is NoteElement -> {
                    val index = noteElements.indexOf(temporalElement)
                    noteElements.remove(temporalElement)

                    val restElement = RestElement(temporalElement.duration)
                    noteElements.add(index, restElement)

                    for (bar in bars) {
                        bar.scoreRenderingElements.find { it == temporalElement }?.let {
                            val index = bar.scoreRenderingElements.indexOf(temporalElement)
                            bar.scoreRenderingElements.remove(temporalElement)
                            bar.scoreRenderingElements.add(restElement)
                        }
                    }
                    return restElement.id

                }
                is RestElement -> {
                    val index = noteElements.indexOf(temporalElement)
                    noteElements.remove(temporalElement)

                    val noteElement = NoteElement(NoteType.C, 5, temporalElement.duration)
                    noteElements.add(index, noteElement)

                    for (bar in bars) {
                        bar.scoreRenderingElements.find { it == temporalElement }?.let {
                            val index = bar.scoreRenderingElements.indexOf(temporalElement)
                            bar.scoreRenderingElements.remove(temporalElement)
                            bar.scoreRenderingElements.add(index, noteElement)
                        }
                    }
                    return noteElement.id
                }
            }
            return id
        }

        return id
    }

}
