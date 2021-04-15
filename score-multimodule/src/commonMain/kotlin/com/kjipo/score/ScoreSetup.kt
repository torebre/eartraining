package com.kjipo.score

import com.kjipo.svg.GlyphData
import mu.KotlinLogging

/**
 * This class produces the rendering sequence that is used to render the score
 */
class ScoreSetup {
    val bars = mutableListOf<BarData>()
    val ties = mutableListOf<TiePair>()
    val beams = mutableListOf<BeamGroup>()
    val context = Context()
    val scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    private val logger = KotlinLogging.logger {}


    fun buildWithMetaData(): RenderingSequenceWithMetaData {
        val renderingSequence = build()
        val highlightElementMap = mutableMapOf<String, Collection<String>>()

        bars.flatMap { it.scoreRenderingElements }.forEach {
            if(it is HighlightableElement) {

                logger.debug { "Highlight: ${it.getIdsOfHighlightElements()}" }

                highlightElementMap[it.id] = it.getIdsOfHighlightElements()
            }

            scoreRenderingElements.add(it)
        }

        logger.debug { "Bars in score: ${bars.size}. Bars in score setup: ${bars.size}" }

        return RenderingSequenceWithMetaData(renderingSequence, highlightElementMap)
    }

    fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        val definitionMap = mutableMapOf<String, GlyphData>()

        val highlightElementsMap = mutableMapOf<String, Collection<String>>()

        // TODO Should also add glyphs from note groups
        val scoreRenderingElements = bars.flatMap { it.scoreRenderingElements }.toList()
        scoreRenderingElements.filter { it is NoteElement }
            .map { it as NoteElement }
            .forEach {
                definitionMap.putAll(it.getGlyphs())
            }

        scoreRenderingElements.filter { it is HighlightableElement }
            .map { it as HighlightableElement }
            .forEach {
                highlightElementsMap[it.id] = it.getIdsOfHighlightElements()
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
                logger.error { "Notes not found. First note: $firstNote. Second note: $lastNote" }
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

            val beamElement = BeamElement(
                "beam-${beamCounter++}",
                Pair(firstStem.boundingBox.xMax, firstStem.boundingBox.yMin),
                Pair(stopX - startX, stopY - startY), firstNote.renderGroup
            )

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

        renderingSequences.add(
            RenderingSequence(
                listOf(RenderGroup(renderingElements, null)),
                determineViewBox(renderingElements),
                definitionMap
            )
        )
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
                definitions[it.key] = it.value
            }
        }

        // TODO Viewbox will be wrong since translations are not taken into account
        return RenderingSequence(
            renderGroups,
            determineViewBox(renderGroups.flatMap { it.renderingElements }),
            definitions
        )
    }


//    fun stemUp(noteId: String): Stem {
//        val noteElement = findNoteElement(noteId, bars)
//
//        if (noteElement == null) {
//            return Stem.UP
//        }
//
//        // If the note is part of a beam group, set it to have the stem in the same direction as the first note in the group
//        for (beamGroup in beams) {
//            if (beamGroup.noteIds.contains(noteId)) {
//                findNoteElement(beamGroup.noteIds.first(), bars)?.let { firstNoteInBeamGroup ->
//                    return context.stemUp(
//                        ScoreHandlerUtilities.getPitch(
//                            firstNoteInBeamGroup.note,
//                            firstNoteInBeamGroup.octave
//                        )
//                    )
//                }
//            }
//        }
//        return Stem.UP
//    }


}
