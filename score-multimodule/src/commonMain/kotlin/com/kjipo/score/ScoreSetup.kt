package com.kjipo.score

import com.kjipo.handler.BeamGroup
import com.kjipo.handler.Score
import com.kjipo.svg.GlyphData
import mu.KotlinLogging

/**
 * This class produces the rendering sequence that is used to render the score
 */
class ScoreSetup(private val score: Score) {
    val context = Context()
    val scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    private var tieElementCounter = 0

    private val logger = KotlinLogging.logger {}


    fun buildWithMetaData(): RenderingSequenceWithMetaData {
        val renderingSequence = build()
        val highlightElementMap = mutableMapOf<String, Collection<String>>()

        scoreRenderingElements.forEach {
            if (it is HighlightableElement) {
                highlightElementMap[it.id] = it.getIdsOfHighlightElements()
            }
        }

        return RenderingSequenceWithMetaData(renderingSequence, highlightElementMap)
    }

    private fun build(): RenderingSequence {
        // TODO The position will be wrong when there are multiple bars
        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        val definitionMap = mutableMapOf<String, GlyphData>()

        val bars = mutableListOf<BarData>()

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 250

        score.bars.forEach { bar ->
            val currentBar = BarData(context, bar, barXoffset, barYoffset)

            bars.add(currentBar)
            val renderingSequence = currentBar.build()
            barXoffset += barXspace
            barYoffset += barYspace
            renderingSequences.add(renderingSequence)

            renderingSequence.definitions.forEach {
                if (!definitionMap.containsKey(it.key)) {
                    definitionMap[it.key] = it.value
                }
            }
        }

        bars.flatMap { it.scoreRenderingElements }.forEach {
            scoreRenderingElements.add(it)
        }

        handleBeams(renderingElements, bars)
        handleTies(renderingElements)

        renderingSequences.add(
            RenderingSequence(
                renderingElements,
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
                if (firstNote is TranslatedRenderingElement && secondNote is TranslatedRenderingElement) {
                    setupTie(firstNote, secondNote)
                } else {
                    logger.error { "Unexpected rendering element type" }
                    null
                }
            }
        }.filterNotNull()
            .forEach {
                it.toRenderingElement()
                    .forEach { positionedRenderingElement -> renderingElements.add(positionedRenderingElement) }
            }
    }

    private fun setupTie(firstStem: TranslatedRenderingElement, lastStem: TranslatedRenderingElement): TieElement {
        val tieElement =
            TieElement(
                "tie-element-$tieElementCounter",
                lastStem.translation.xShift.toDouble(),
                lastStem.translation.yShift.toDouble()
            )

        // TODO Set up correct positioning
//        tieElement.xPosition = firstStem.translation.xShift
//        tieElement.yPosition = firstStem.translation.yShift

        return tieElement
    }


    private fun handleBeams(renderingElements: MutableList<PositionedRenderingElement>, bars: List<BarData>) {
        var beamCounter = 0

        score.beamGroups.map { beam ->
            val beamId = "beam-${beamCounter++}"
            setupBeamElement(beam, beamId, bars)?.let {
                renderingElements.addAll(it.toRenderingElement())
            }
        }
    }


    private fun setupBeamElement(beamGroup: BeamGroup, beamId: String, bars: List<BarData>): BeamElement? {
        // TODO This will not create a proper looking bar in many cases
        val firstNote = findNoteElement(beamGroup.notes.first().id, bars)
        val lastNote = findNoteElement(beamGroup.notes.last().id, bars)

        if (firstNote == null || lastNote == null) {
            logger.error { "Notes not found. First note: $firstNote. Second note: $lastNote" }
            return null
        }

        val firstStem = firstNote.getStem()
        val lastStem = lastNote.getStem()

        var startX: Double
        var startY: Double

        firstNote.let {
            startX = firstStem.boundingBox.xMax + (it.translation?.xShift ?: 0).toDouble()
            startY = firstStem.boundingBox.yMin + (it.translation?.yShift ?: 0).toDouble()
        }

        var stopX: Double
        var stopY: Double
        lastNote.let {
            stopX = lastStem.boundingBox.xMax + (it.translation?.xShift ?: 0).toDouble()
            stopY = lastStem.boundingBox.yMin + (it.translation?.yShift ?: 0).toDouble()
        }

        return BeamElement(
            beamId,
            Pair(firstStem.boundingBox.xMax, firstStem.boundingBox.yMin),
            Pair(stopX - startX, stopY - startY)
        )
    }

    private fun findNoteElement(noteId: String, bars: List<BarData>) = bars.flatMap { it.scoreRenderingElements }
        .filterIsInstance<NoteElement>()
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
            determineViewBox(renderGroups),
            definitions
        )
    }

}
