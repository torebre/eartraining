package com.kjipo.score

import com.github.aakira.napier.Napier
import com.kjipo.svg.GlyphData

class ScoreSetup {
    val bars = mutableListOf<BarData>()
    val ties = mutableListOf<TiePair>()
    val beams = mutableListOf<BeamGroup>()


    fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        val definitionMap = mutableMapOf<String, GlyphData>()

        bars.flatMap { it.scoreRenderingElements }
                .filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach { it ->
                    definitionMap.putAll(it.getGlyphs())
                }

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 250

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
            val firstNote = findNoteElement(beam.noteIds.first(), bars)
            val lastNote = findNoteElement(beam.noteIds.last(), bars)

            if (firstNote == null || lastNote == null) {
                Napier.e("Notes not found. First note: $firstNote. Second note: $lastNote")
                return@map
            }

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


    private fun findNoteElement(noteId: String, bars: List<BarData>) = bars.flatMap { it.scoreRenderingElements }
            .filter { it is NoteElement }
            .map { it as NoteElement }
            .find { it.id == noteId }


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


}
