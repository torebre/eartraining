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

    private val barData = mutableListOf<BarData>()

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


    private fun clearData() {
        barData.clear()
    }

    private fun build(): RenderingSequence {
        // TODO The position will be wrong when there are multiple bars
        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        val definitionMap = mutableMapOf<String, GlyphData>()

        clearData()

        var barXoffset = 0.0
        var barYoffset = 0.0

        score.bars.forEach { bar ->
            val currentBar = BarData(context, bar, barXoffset, barYoffset)

            barData.add(currentBar)
            val renderingSequence = currentBar.build()
            barXoffset += context.barXspace
            barYoffset += context.barYspace
            renderingSequences.add(renderingSequence)

            renderingSequence.definitions.forEach {
                if (!definitionMap.containsKey(it.key)) {
                    definitionMap[it.key] = it.value
                }
            }
        }

        barData.flatMap { it.scoreRenderingElements }.forEach {
            scoreRenderingElements.add(it)
        }

        handleBeams(renderingElements, barData)
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
        score.ties.mapNotNull { tie ->
            val firstNote =
                scoreRenderingElements.find { it is NoteElement && it.id == tie.first.id }
            val secondNote =
                scoreRenderingElements.find { it is NoteElement && it.id == tie.second.id }

            if (firstNote !is ElementCanBeTied || secondNote !is ElementCanBeTied) {
                logger.error { "Did not find all elements in tie. First element: $firstNote. Second element: $secondNote" }
                null
            } else {
                setupTie(firstNote, secondNote)
            }
        }.flatten()
            .forEach {
                it.toRenderingElement()
                    .forEach { positionedRenderingElement -> renderingElements.add(positionedRenderingElement) }
            }
    }


    private fun getBarForElement(scoreElementMarker: ScoreElementMarker): BarData? {
        return barData.find { barData -> barData.scoreRenderingElements.contains(scoreElementMarker) }
    }


    private fun setupTie(fromElement: ElementCanBeTied, toElement: ElementCanBeTied): Collection<TieElement> {
        // TODO Need to be able to determine when to use top and bottom position
        val useTop = false

        val startBar = getBarForElement(fromElement)
        val endBar = getBarForElement(toElement)

        if (startBar == null || endBar == null) {
            logger.error { "Start or end bar of tie missing. Start: {startBar}. End: {endBar}" }
            return emptyList()
        }

        return if (startBar.barYoffset != endBar.barYoffset) {
            setTwoTiesForTieSpanningRows(fromElement, startBar, toElement, endBar, useTop)
        } else {
            setupTieForSingleRow(fromElement, toElement, useTop)
        }
    }

    private fun setupTieForSingleRow(
        fromElement: ElementCanBeTied,
        toElement: ElementCanBeTied,
        useTop: Boolean
    ): List<TieElement> {
        val fromCoordinates = fromElement.getTieCoordinates(useTop)
        val toCoordinates = toElement.getTieCoordinates(useTop)

        val tieElement =
            TieElement(
                context.getAndIncrementTieCounter(),
                fromCoordinates,
                toCoordinates,
                useTop
            )

        return listOf(tieElement)
    }

    private fun setTwoTiesForTieSpanningRows(
        fromElement: ElementCanBeTied,
        startBarData: BarData,
        toElement: ElementCanBeTied,
        endBarData: BarData,
        useTop: Boolean
    ): List<TieElement> {
        val fromCoordinates = fromElement.getTieCoordinates(useTop)
        val toCoordinates = toElement.getTieCoordinates(useTop)
        val firstTie = TieElement(
            context.getAndIncrementTieCounter(),
            fromCoordinates,
            Pair(startBarData.getBarXEnd(), fromCoordinates.second),
            useTop
        )
        val secondTie = TieElement(
            context.getAndIncrementTieCounter(),
            Pair(endBarData.getBarXStart(), toCoordinates.second), toCoordinates,
            useTop
        )

        return listOf(firstTie, secondTie)
    }


    private fun handleBeams(renderingElements: MutableList<PositionedRenderingElement>, bars: List<BarData>) {
        score.beamGroups.map { beam ->
            val beamId = context.getAndIncrementBeamCounter()
            setupBeamElement(beam, beamId, bars)?.let {
                renderingElements.addAll(it.toRenderingElement())
            }
        }
    }


    private fun setupBeamElement(
        beamGroup: BeamGroup,
        beamId: String,
        bars: List<BarData>
    ): BeamElementAbsolutePosition? {
        // TODO This will not create a proper looking bar in many cases
        // TODO Need to handle beams with multiple lines
        val firstNote = findNoteElement(beamGroup.notes.first().id, bars)
        val lastNote = findNoteElement(beamGroup.notes.last().id, bars)

        if (firstNote == null || lastNote == null) {
            logger.error { "Notes not found. First note: $firstNote. Second note: $lastNote" }
            return null
        }

        val firstStem = firstNote.getStem()
        val lastStem = lastNote.getStem()

        if (firstStem == null || lastStem == null) {
            logger.error { "Need stems for both notes included in beam. Beam group: $beamGroup" }
            return null
        }

        val (startX, startY) = with(firstNote) {
            Pair(
                firstStem.boundingBox.xMax - DEFAULT_STEM_WIDTH + (translation?.xShift ?: 0).toDouble(),
                firstStem.boundingBox.yMin + (translation?.yShift ?: 0).toDouble()
            )
        }

        val (stopX, stopY) = with(lastNote) {
            Pair(
                lastStem.boundingBox.xMax + (translation?.xShift ?: 0).toDouble(),
                lastStem.boundingBox.yMin + (translation?.yShift ?: 0).toDouble()
            )
        }

        return BeamElementAbsolutePosition(
            beamId,
            Pair(startX, startY),
            Pair(stopX, stopY)
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
