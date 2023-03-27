package com.kjipo.score

import com.kjipo.handler.BeamGroup
import com.kjipo.handler.BeamLine
import com.kjipo.handler.Score
import com.kjipo.svg.GlyphData
import mu.KotlinLogging
import kotlin.math.absoluteValue

/**
 * This class produces the rendering sequence that is used to render the score
 */
class ScoreSetup(private val score: Score) {
    val context = Context(score)
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
        val definitionMap = mutableMapOf<String, GlyphData>()

        val renderingSequences = mutableListOf<RenderingSequence>()
        val renderingElements = mutableListOf<PositionedRenderingElement>()

        clearData()

        var barXoffset = 0.0
        var barYoffset = 0.0

        // Do layout
        score.bars.forEach { bar ->
            val currentBar = BarData(context, bar, barXoffset, barYoffset)

            barData.add(currentBar)
            currentBar.doLayout()
            barXoffset += context.barXspace
            barYoffset += context.barYspace
        }

        handleBeams(renderingElements, barData)
        handleTies(renderingElements)

        barData.forEach { currentBar ->
            val renderingSequence = currentBar.getRenderingSequence()

            renderingSequences.add(renderingSequence)

            renderingSequence.definitions.forEach {
                if (!definitionMap.containsKey(it.key)) {
                    definitionMap[it.key] = it.value
                }
            }
            scoreRenderingElements.addAll(currentBar.scoreRenderingElements)
        }

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
            setupBeamElement(beam, beamId, bars).let { beamElements ->
                beamElements.forEach {
                    renderingElements.addAll(it.toRenderingElement())
                }
            }
        }
    }


    private fun setupBeamElement(
        beamGroup: BeamGroup,
        beamId: String,
        bars: List<BarData>
    ): List<BeamElementAbsolutePosition> {
        return beamGroup.beamLines.mapNotNull { beamLine ->
            handleSingleBeamLine(beamLine, beamId, bars)
        }
    }

    private fun handleSingleBeamLine(
        beamLine: BeamLine,
        beamId: String,
        bars: List<BarData>
    ): BeamElementAbsolutePosition? {
        val elementsToIncludeInBeam = beamLine.elements.mapNotNull { beamableElement ->
            findBeamableElement(beamableElement.id, bars)
        }

        val firstNote = elementsToIncludeInBeam.first()
        val lastNote = elementsToIncludeInBeam.last()

        val beamCalculation = BeamCalculation(firstNote, lastNote)

        if (beamCalculation.startCoordinates == null || beamCalculation.stopCoordinates == null) {
            logger.error { "Need coordinates of first and last notes in beam group" }
            return null
        }

        // The y-coordinate increases when moving downwards
        val delta =
            (beamCalculation.stopCoordinates.second - beamCalculation.startCoordinates.second) / (beamCalculation.stopCoordinates.first - beamCalculation.startCoordinates.first)

        elementsToIncludeInBeam.forEachIndexed { index, element ->
            if (index != 0 && index != elementsToIncludeInBeam.size - 1) {
                // Element that is inside the beam, not one of the endpoints
                element.getStem()?.let { stem ->
                    val elementTranslationX = element.translation?.xShift ?: 0.0
                    val elementTranslationY = element.getVerticalOffsetForStemStart()

                    // Update the stem height of the element so that it touches the beam line
                    val updatedStemHeight = if (element.isStemUp()) {
                        ((elementTranslationX - beamCalculation.firstNoteXTranslation) * delta + elementTranslationY + firstNote.getStemHeight()).absoluteValue
                    } else {
                        val beamPoint =
                            (elementTranslationX - beamCalculation.firstNoteXTranslation) * delta + firstNote.getVerticalOffsetForStemStart() + firstNote.getStemHeight()

                        beamPoint - element.getVerticalOffsetForStemStart()
                    }

                    element.updateStemHeight(updatedStemHeight)

                    // TODO

                }
            }
        }

        // TODO This will not create a proper looking bar in many cases
        // TODO Need to handle beams with multiple lines

        return BeamElementAbsolutePosition(
            beamId,
            Pair(beamCalculation.startCoordinates.first, beamCalculation.startCoordinates.second),
            Pair(beamCalculation.stopCoordinates.first, beamCalculation.stopCoordinates.second)
        )
    }


    /**
     * Contains the points that make up a square which is the beam element.
     */
    private class BeamCalculation(firstElement: ElementCanBeInBeamGroup, lastElement: ElementCanBeInBeamGroup) {
        val firstNoteXTranslation: Double
        val lastNoteXTranslation: Double
        val firstNoteYTranslation: Double
        val lastNoteYTranslation: Double
        val startCoordinates: Pair<Double, Double>?
        val stopCoordinates: Pair<Double, Double>?

        init {
            firstNoteXTranslation = (firstElement.translation?.xShift ?: 0.0).toDouble()
            lastNoteXTranslation = (lastElement.translation?.xShift ?: 0.0).toDouble()

            firstNoteYTranslation = firstElement.getVerticalOffsetForStemStart()
            lastNoteYTranslation = lastElement.getVerticalOffsetForStemStart()

            startCoordinates = firstElement.getAbsoluteCoordinatesForEndpointOfStem()?.let {
                Pair(
                    it.first, if (firstElement.isStemUp()) {
                        it.second + DEFAULT_BEAM_HEIGHT
                    } else {
                        it.second
                    }
                )
            }
            stopCoordinates = lastElement.getAbsoluteCoordinatesForEndpointOfStem()?.let {
                Pair(
                    it.first, if (lastElement.isStemUp()) {
                        it.second + DEFAULT_BEAM_HEIGHT
                    } else {
                        it.second
                    }
                )
            }
        }
    }

    private fun findBeamableElement(noteId: String, bars: List<BarData>) = bars.flatMap { it.scoreRenderingElements }
        .filterIsInstance<ElementCanBeInBeamGroup>()
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
