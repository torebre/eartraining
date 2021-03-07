package com.kjipo.handler

import com.kjipo.score.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

/**
 * Stores a sequence of temporal elements, and can produce a score based on them.
 */
class ScoreHandlerWithReducedLogic(score: Score) {

    var score: Score = score
        set(value) {
            field = value
            cachedBuild = null
            highlightElementMap.clear()
            build()
        }

    private var cachedBuild: RenderingSequenceWithMetaData? = null

    val highlightElementMap = mutableMapOf<String, Collection<String>>()

    private val logger = KotlinLogging.logger {}

    fun getScoreAsJson() =
        truncateNumbers(
            Json.encodeToString(
                RenderingSequence.serializer(),
                cachedBuild?.renderingSequence ?: build().renderingSequence
            )
        )

    private fun build(): RenderingSequenceWithMetaData {
        val scoreSetup = ScoreSetup()

        for (bar in score.bars) {
            val barData = BarData(scoreSetup.context)
            scoreSetup.bars.add(barData)

            for (element in bar.scoreHandlerElements) {
                addTemporalElement(element, barData, highlightElementMap, scoreSetup)
            }

            logger.debug { "Highlight map in score handler: $highlightElementMap" }
        }

        val renderingSequence = scoreSetup.build()

        logger.debug { "Bars in score: ${score.bars.size}. Bars in score setup: ${scoreSetup.bars.size}" }

        return RenderingSequenceWithMetaData(renderingSequence, highlightElementMap)
    }

    private fun addTemporalElement(
        element: ScoreHandlerElement,
        currentBar: BarData,
        highlightElementMap: MutableMap<String, Collection<String>>,
        scoreSetup: ScoreSetup
    ) {
        val temporalElement = createTemporalElement(element, scoreSetup.context)
        currentBar.scoreRenderingElements.add(temporalElement)
        if (temporalElement is HighlightableElement) {
            highlightElementMap[element.id] = temporalElement.getIdsOfHighlightElements()
        }
    }

    private fun addBeams(barData: BarData): MutableList<BeamGroup> {
        val beamGroups = mutableListOf<BeamGroup>()
        val notesInBeamGroup = mutableListOf<NoteElement>()

        for (scoreRenderingElement in barData.scoreRenderingElements) {
            if (scoreRenderingElement is NoteElement && scoreRenderingElement.duration == Duration.EIGHT) {
                notesInBeamGroup.add(scoreRenderingElement)
            } else if (scoreRenderingElement is RestElement) {
                // Some other note element or a rest
                if (notesInBeamGroup.size > 1) {
                    notesInBeamGroup.forEach { it.partOfBeamGroup = true }
                    beamGroups.add(BeamGroup(notesInBeamGroup.map { it.id }))
                }
                notesInBeamGroup.clear()
            }
        }
        return beamGroups
    }

//    private fun addAndTie(
//        element: NoteOrRest,
//        durations: List<Duration>,
//        barData: BarData,
//        previous: ScoreRenderingElement? = null,
//        scoreSetup: ScoreSetup
//    ): ScoreRenderingElement? {
//        var previousInternal = previous
//        for (duration in durations) {
//            val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
//                transformToNoteAndAccidental(element.noteType).let { noteAndAccidental ->
//                    NoteElement(
//                        noteAndAccidental.first,
//                        element.octave,
//                        duration,
//                        scoreSetup.context
//                    ).also { it.accidental }
//                }
//            } else {
//                RestElement(duration, scoreSetup.context)
//            }
//
//            barData.scoreRenderingElements.add(scoreRenderingElement)
//
//            if (previous != null && scoreRenderingElement is NoteElement) {
//                scoreSetup.ties.add(TiePair(previous as NoteElement, scoreRenderingElement))
//            }
//            previousInternal = scoreRenderingElement
//        }
//
//        return previousInternal
//    }

//    fun addBeams(noteElementIds: List<String>) {
//        val noteElementsToTie = noteElementIds.map { findScoreHandlerElement(it) }
//            .toList()
//        if (noteElementsToTie.any { it == null }) {
//            throw IllegalArgumentException("Not all note elements found. Element IDs: ${noteElementIds}")
//        }
//        beams.add(BeamGroup(noteElementIds))
//    }

    private fun createTemporalElement(element: ScoreHandlerElement, context: Context): ScoreRenderingElement {
        when (element) {
            is NoteOrRest -> {
                return if (element.isNote) {
                    ScoreHelperFunctions.transformToNoteAndAccidental(element.noteType).let { noteAndAccidental ->
                        NoteElement(
                            noteAndAccidental.first,
                            element.octave,
                            element.duration,
                            context,
                            id = element.id
                        ).also {
                            it.accidental = noteAndAccidental.second
                        }
                    }
                } else {
                    RestElement(element.duration, context, id = element.id)
                }

            }
            is NoteGroup -> {
                // TODO Handle duration on note level
                return NoteGroupElement(element.notes, element.notes.first().duration, element.id, context)
            }

        }

    }


}
