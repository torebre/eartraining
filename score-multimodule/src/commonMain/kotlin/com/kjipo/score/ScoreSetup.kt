package com.kjipo.score

import com.kjipo.handler.Score
import com.kjipo.svg.GlyphData
import mu.KotlinLogging

/**
 * This class produces the rendering sequence that is used to render the score
 */
class ScoreSetup(private val score: Score) {
    val bars = mutableListOf<BarData>()

    //    val ties = mutableListOf<TiePair>()
    val beams = mutableListOf<BeamGroup>()
    val context = Context()
    val scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    private var tieElementCounter = 0

    private val logger = KotlinLogging.logger {}


    fun buildWithMetaData(): RenderingSequenceWithMetaData {
        for (bar in score.bars) {
            val barData = BarData(context, bar)
            bars.add(barData)
        }

        val renderingSequence = build()
        val highlightElementMap = mutableMapOf<String, Collection<String>>()
        scoreRenderingElements.forEach {
            if (it is HighlightableElement) {
                highlightElementMap[it.id] = it.getIdsOfHighlightElements()
            }
        }

        logger.debug { "Bars in score: ${bars.size}. Bars in score setup: ${bars.size}" }

        return RenderingSequenceWithMetaData(renderingSequence, highlightElementMap)
    }

    private fun build(): RenderingSequence {
        // TODO The position will be wrong when there are multiple bars
        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        val definitionMap = mutableMapOf<String, GlyphData>()

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 250

        score.bars.forEach { bar ->
            val currentBar = BarData(context, bar, barXoffset, barXoffset).also { bars.add(it) }.build()
            currentBar.definitions.forEach {
                if (!definitionMap.containsKey(it.key)) {
                    definitionMap[it.key] = it.value
                }
            }

            renderingSequences.add(currentBar)
            barXoffset += barXspace
            barYoffset += barYspace
        }

        bars.flatMap { it.scoreRenderingElements }.forEach {
            scoreRenderingElements.add(it)
        }

        handleBeams()
        handleTies(renderingElements)

        renderingSequences.add(
            RenderingSequence(
                listOf(RenderGroup(renderingElements, null)),
                determineViewBox(renderingElements),
                definitionMap
            )
        )
        return mergeRenderingSequences(renderingSequences)
    }

    private fun handleTies(renderingElements: MutableList<PositionedRenderingElement>) {
        // TODO Note group elements can also have ties
        score.ties.map { tie ->
            val firstNote =
                scoreRenderingElements.find { it is NoteElement && it.id == tie.first.id }
                    ?.let { (it as NoteElement).getStem() }
            val secondNote =
                scoreRenderingElements.find { it is NoteElement && it.id == tie.second.id }
                    ?.let { (it as NoteElement).getStem() }

            if (firstNote == null || secondNote == null) {
                logger.error { "Did not find all elements in tie. First element: $firstNote. Second element: $secondNote" }
                null
            } else {
                setupTie(firstNote, secondNote)
            }
        }.filterNotNull()
            .forEach {
                it.toRenderingElement()
                    .forEach { positionedRenderingElement -> renderingElements.add(positionedRenderingElement) }
            }
    }

    private fun setupTie(firstStem: PositionedRenderingElement, lastStem: PositionedRenderingElement): TieElement {
//            var startX = 0.0
//            var startY = 0.0

//            tie.startNote.renderGroup?.let { renderGroup ->
//                renderGroup.transform?.let {
//                    startX = firstStem.boundingBox.xMax //+ it.xShift
//                    startY = firstStem.boundingBox.yMin //+ it.yShift
//                }
//            }

//            var stopX = 0.0
//            var stopY = 0.0
//            tie.endNote.renderGroup?.let { renderGroup ->
//                renderGroup.transform?.let {
//                    stopX = lastStem.boundingBox.xMax //+ it.xShift
//                    stopY = lastStem.boundingBox.yMin //+ it.yShift
//                }
//            }

        val tieElement =
            TieElement("tie-element-$tieElementCounter", lastStem.xPosition.toDouble(), lastStem.yPosition.toDouble())

        tieElement.xPosition = firstStem.xPosition
        tieElement.yPosition = firstStem.yPosition

        return tieElement
    }


    private fun handleBeams() {
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


    companion object {


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
