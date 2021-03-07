package com.kjipo.handler

import com.kjipo.score.*
import kotlinx.serialization.json.Json
import mu.KotlinLogging

/**
 * Stores a sequence of temporal elements, and can produce a score based on them.
 */
class ScoreHandler : ScoreHandlerInterface {
    private val scoreHandlerElements = mutableListOf<ScoreHandlerElement>()
    private var idCounter = 0
    private val ticksPerBar = 4 * TICKS_PER_QUARTER_NOTE

    private val logger = KotlinLogging.logger {}

    private val beams = mutableListOf<BeamGroup>()

    private var trimEndBars = true

    private var scoreSetup = ScoreSetup()

    private var cachedBuild: RenderingSequenceWithMetaData? = null


    override fun getScoreAsJson() =
        truncateNumbers(
            Json.encodeToString(
                RenderingSequence.serializer(),
                cachedBuild?.renderingSequence ?: build().renderingSequence
            )
        )


    fun clear() {
        idCounter = 0
        scoreHandlerElements.clear()
        beams.clear()
        scoreSetup = ScoreSetup()
    }


    fun build(): RenderingSequenceWithMetaData {

        // TODO Probably does not make sense to create a new one since the context inside it is used elsewhere
        scoreSetup = ScoreSetup()


        // This is to make the IDs the same every time the score is rendered. It is needed to make tests for the diff-functionality pass
        BarData.barNumber = 0
        BarData.stemCounter = 0
        ExtraBarLinesElement.idCounter = 0

        // TODO Not necessary to have this here. Beams should be computed automatically below
        scoreSetup.beams.addAll(beams)
        var remainingTicksInBar = ticksPerBar
        var currentBar = BarData(scoreSetup.context)
        currentBar.clef = Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)
        val bars = mutableListOf(currentBar)

        logger.debug { "Score handler elements: $scoreHandlerElements" }

        val highlightElementMap = mutableMapOf<String, Collection<String>>()

        for (element in scoreHandlerElements) {

            when (element) {
                is NoteOrRest -> {
                    val ticksNeededForElement = element.duration.ticks

                    if (remainingTicksInBar == 0) {
                        // No more room in bar, start on a new one
                        remainingTicksInBar = ticksPerBar
                        currentBar = BarData(scoreSetup.context)
                        bars.add(currentBar)
                    }

                    when {
                        remainingTicksInBar < ticksNeededForElement -> {
                            val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar)
                            val durationsInNextBar =
                                ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar)

                            val previous =
                                addAndTie(element, durationsInCurrentBar, currentBar, scoreSetup = scoreSetup)
                            remainingTicksInBar += ticksPerBar - ticksNeededForElement
                            currentBar = BarData(scoreSetup.context)
                            bars.add(currentBar)
                            addAndTie(element, durationsInNextBar, currentBar, previous, scoreSetup = scoreSetup)
                        }
                        else -> {
                            remainingTicksInBar -= ticksNeededForElement


                            val temporalElement = createTemporalElement(element, scoreSetup.context)
                            currentBar.scoreRenderingElements.add(temporalElement)
                            if (temporalElement is HighlightableElement) {
                                highlightElementMap[element.id] = temporalElement.getIdsOfHighlightElements()
                            }
                        }
                    }
                }
                is NoteGroup -> {
                    // TODO For now assuming that all notes in the group have the same duration
                    val duration = element.notes.first().duration
                    val ticksNeededForElement = duration.ticks

                    if (remainingTicksInBar == 0) {
                        // No more room in bar, start on a new one
                        remainingTicksInBar = ticksPerBar
                        currentBar = BarData(scoreSetup.context)
                        bars.add(currentBar)
                    }

                    when {
                        // TODO Handle situation when element crosses bar lines

//                        remainingTicksInBar < ticksNeededForElement -> {
//                            val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar)
//                            val durationsInNextBar =
//                                ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar)
//
//                            val previous =
//                                addAndTie(element, durationsInCurrentBar, currentBar, scoreSetup = scoreSetup)
//                            remainingTicksInBar += ticksPerBar - ticksNeededForElement
//                            currentBar = BarData()
//                            bars.add(currentBar)
//                            addAndTie(element, durationsInNextBar, currentBar, previous, scoreSetup = scoreSetup)
//                        }
                        else -> {
                            remainingTicksInBar -= ticksNeededForElement
                            val temporalElement = createTemporalElement(element, scoreSetup.context)
                            currentBar.scoreRenderingElements.add(temporalElement)

                            if (temporalElement is HighlightableElement) {
                                highlightElementMap[element.id] = temporalElement.getIdsOfHighlightElements()
                            }
                        }
                    }
                }
            }
        }

        logger.debug { "Number of bars: ${bars.size}. Remaining ticks in bar: $remainingTicksInBar" }

        var lastBarTrimmed = false
        if (trimEndBars && bars.size > 1) {
            val barsBeforeTrimming = bars.size
            trimBars(bars)
            lastBarTrimmed = barsBeforeTrimming != bars.size
        }

        logger.debug { "After trimming. Number of bars: ${bars.size}. Remaining ticks in bar: $remainingTicksInBar" }

        if (!lastBarTrimmed) {
            fillInLastBar(bars, remainingTicksInBar, scoreSetup.context)
        }

        logger.debug { "After fill in. Number of bars: ${bars.size}. Remaining ticks in bar: $remainingTicksInBar" }

        bars.forEach {
            scoreSetup.beams.addAll(addBeams(it))
            logger.debug { "Bar data: $it" }
        }
        scoreSetup.bars.addAll(bars)

        return RenderingSequenceWithMetaData(scoreSetup.build(), highlightElementMap)
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

    private fun addAndTie(
        element: NoteOrRest,
        durations: List<Duration>,
        barData: BarData,
        previous: ScoreRenderingElement? = null,
        scoreSetup: ScoreSetup
    ): ScoreRenderingElement? {
        var previousInternal = previous
        for (duration in durations) {
            val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
                transformToNoteAndAccidental(element.noteType).let { noteAndAccidental ->
                    NoteElement(
                        noteAndAccidental.first,
                        element.octave,
                        duration,
                        scoreSetup.context
                    ).also { it.accidental }
                }
            } else {
                RestElement(duration, scoreSetup.context)
            }

            barData.scoreRenderingElements.add(scoreRenderingElement)

            if (previous != null && scoreRenderingElement is NoteElement) {
                scoreSetup.ties.add(TiePair(previous as NoteElement, scoreRenderingElement))
            }
            previousInternal = scoreRenderingElement
        }

        return previousInternal
    }

    fun addBeams(noteElementIds: List<String>) {
        val noteElementsToTie = noteElementIds.map { findScoreHandlerElement(it) }
            .toList()
        if (noteElementsToTie.any { it == null }) {
            throw IllegalArgumentException("Not all note elements found. Element IDs: ${noteElementIds}")
        }
        beams.add(BeamGroup(noteElementIds))
    }

    private fun createTemporalElement(element: ScoreHandlerElement, context: Context): ScoreRenderingElement {
        when (element) {
            is NoteOrRest -> {
                return if (element.isNote) {
                    transformToNoteAndAccidental(element.noteType).let { noteAndAccidental ->
                        NoteElement(noteAndAccidental.first, element.octave, element.duration, context).also {
                            it.accidental = noteAndAccidental.second
                        }
                    }
                } else {
                    RestElement(element.duration, context)
                }

            }
            is NoteGroup -> {
                // TODO Handle duration on note level
                return NoteGroupElement(element.notes, element.notes.first().duration, scoreSetup.context)
            }

        }

    }

    private fun fillInLastBar(bars: MutableList<BarData>, ticksRemainingInBar: Int, context: Context) {
        if (bars.isEmpty() || ticksRemainingInBar == 0) {
            return
        }

        bars.last().let { lastBar ->
            ScoreHandlerUtilities.splitIntoDurations(ticksRemainingInBar).forEach {
                val scoreHandlerElement =
                    NoteOrRest((++idCounter).toString(), it, false, 5, NoteType.C)
                scoreHandlerElements.add(scoreHandlerElement)
                lastBar.scoreRenderingElements.add(RestElement(it, context))
            }
        }
    }

    private fun trimBars(bars: MutableList<BarData>) {
        if (bars.isEmpty()) {
            return
        }
        bars.takeLastWhile { bar -> bar.scoreRenderingElements.all { it is RestElement } }.forEach { bars.remove(it) }
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {
        scoreHandlerElements.find { it.id == id }?.let {
            when (it) {
                is NoteOrRest -> {
                    handleMoveNoteForNoteOrRest(it, up)

                }
                // TODO Need to handle notes inside note group


            }
        }

    }

    private fun handleMoveNoteForNoteOrRest(noteOrRestElement: NoteOrRest, up: Boolean) {
        if (!noteOrRestElement.isNote) {
            // Cannot move an element which is not a note
            return
        }

        if (up) {
            if (noteOrRestElement.noteType == NoteType.H) {
                noteOrRestElement.noteType = NoteType.C
                ++noteOrRestElement.octave
            } else {
                noteOrRestElement.noteType =
                    NoteType.values()[(noteOrRestElement.noteType.ordinal + 1) % NoteType.values().size]
            }
        } else {
            if (noteOrRestElement.noteType == NoteType.C) {
                noteOrRestElement.noteType = NoteType.H
                --noteOrRestElement.octave
            } else {
                noteOrRestElement.noteType =
                    NoteType.values()[(NoteType.values().size + noteOrRestElement.noteType.ordinal - 1) % NoteType.values().size]
            }
        }
    }

    override fun getIdOfFirstSelectableElement() = scoreHandlerElements.firstOrNull()?.let { it.id }

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String? {
        return scoreHandlerElements.find { it.id == activeElement }?.let { scoreHandlerElement ->
            val indexOfElement = scoreHandlerElements.indexOf(scoreHandlerElement)
            if (lookLeft) {
                if (indexOfElement == 0) {
                    scoreHandlerElement.id
                } else {
                    scoreHandlerElements[indexOfElement - 1].id
                }
            } else {
                if (indexOfElement == scoreHandlerElements.size - 1) {
                    scoreHandlerElement.id
                } else {
                    scoreHandlerElements[indexOfElement + 1].id
                }
            }
        }
    }

    override fun updateDuration(id: String, keyPressed: Int) {
        scoreHandlerElements.find { it.id == id }?.let {
            when (it) {
                is NoteOrRest -> it.duration = ScoreHandlerUtilities.getDuration(keyPressed)

                // TODO Need to handle notes inside note group

            }

        }
    }

    override fun insertNote(activeElement: String, keyPressed: Int) =
        insertNote(activeElement, ScoreHandlerUtilities.getDuration(keyPressed))

    fun insertNote(activeElement: String, duration: Duration): String? {
        return scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreHandlerElements.indexOf(element) + 1
            scoreHandlerElements.add(
                insertIndex,
                NoteOrRest((++idCounter).toString(), duration, true, 5, NoteType.C)
            )
            return scoreHandlerElements[insertIndex].id
        }
    }

    override fun insertNote(keyPressed: Int) = insertNote(ScoreHandlerUtilities.getDuration(keyPressed))

    fun insertNote(duration: Duration): String {
        scoreHandlerElements.add(NoteOrRest((++idCounter).toString(), duration, true, 5, NoteType.C))
        return scoreHandlerElements.last().id
    }

    fun insertNote(duration: Duration, octave: Int, noteType: NoteType): String {
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                true,
                octave,
                noteType
            )
        )
        return scoreHandlerElements.last().id
    }

    fun insertRest(duration: Duration): String {
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                false,
                5,
                NoteType.C
            )
        )
        return scoreHandlerElements.last().id
    }

    fun insertChord(duration: Duration, elements: Collection<NoteSequenceElement.NoteElement>) {
        val noteGroupElement = NoteGroup((++idCounter).toString(), elements.map {
            // TODO Need to handle accidentals here?
            NoteSymbol((++idCounter).toString(), duration, it.octave, it.note)
        }.toList())

        scoreHandlerElements.add(noteGroupElement)
    }


    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        scoreHandlerElements.find { it.id == idOfElementToReplace }?.let {
            when (it) {
                is NoteOrRest -> it.isNote = !it.isNote
                // TODO Need to handle notes inside note groups
            }

        }
        return idOfElementToReplace
    }

    override fun deleteElement(id: String) {
        scoreHandlerElements.find { it.id == id }?.let {
            scoreHandlerElements.remove(it)
        }
    }

    fun findNoteType(id: String): NoteType? {
        return scoreHandlerElements.find { it.id == id }?.let {
            when (it) {
                is NoteOrRest -> {
                    if (it.isNote) {
                        it.noteType
                    } else {
                        null
                    }
                }
                // TODO Need to handle notes inside note group
                else -> null

            }
        }
    }

    fun findScoreHandlerElement(id: String) = scoreHandlerElements.find { it.id == id }

    fun getScoreHandlerElements() = scoreHandlerElements

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int): String? {
        scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreHandlerElements.indexOf(element) + 1
            scoreHandlerElements.add(
                insertIndex,
                NoteOrRest((++idCounter).toString(), duration, true, 5, NoteType.C)
            )
            return scoreHandlerElements[insertIndex].id
        }
        return null
    }

    override fun addNoteGroup(duration: Duration, pitches: List<ScoreHandlerInterface.GroupNote>): String {
        val groupNotes = pitches.map {
            NoteSymbol((++idCounter).toString(), duration, it.octave, it.noteType)
        }.toList()
        scoreHandlerElements.add(NoteGroup((++idCounter).toString(), groupNotes))
        return scoreHandlerElements.last().id
    }

    override fun getHighlightElementsMap(): Map<String, Collection<String>> {
        return cachedBuild?.highlightElementsMap ?: build().highlightElementsMap
    }

    override fun insertRest(activeElement: String, duration: Duration): String? {
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                false,
                5,
                NoteType.C,
            )
        )
        return scoreHandlerElements.last().id
    }


    override fun applyOperation(operation: ScoreOperation): String? {
        when (operation) {
            is InsertNote -> {
                handleInsertNote(operation)


            }
            else -> {
                // TODO

            }

            // TODO


        }

        // TODO
        return null
    }

        private fun handleInsertNote(insertNote: InsertNote) {
            // Default to quarter if nothing is set
            val duration = insertNote.duration ?: Duration.QUARTER
            if (insertNote.id != null) {
                insertNote(insertNote.id, duration)
            }
            insertNote(duration)
        }

    private fun transformToNoteAndAccidental(noteType: NoteType): Pair<GClefNoteLine, Accidental?> {
        return when (noteType) {
            NoteType.A -> Pair(GClefNoteLine.A, null)
            NoteType.A_SHARP -> Pair(GClefNoteLine.A, Accidental.SHARP)
            NoteType.H -> Pair(GClefNoteLine.H, null)
            NoteType.C -> Pair(GClefNoteLine.C, null)
            NoteType.C_SHARP -> Pair(GClefNoteLine.C, Accidental.SHARP)
            NoteType.D -> Pair(GClefNoteLine.D, null)
            NoteType.D_SHARP -> Pair(GClefNoteLine.D, Accidental.SHARP)
            NoteType.E -> Pair(GClefNoteLine.E, null)
            NoteType.F -> Pair(GClefNoteLine.F, null)
            NoteType.F_SHARP -> Pair(GClefNoteLine.F, Accidental.SHARP)
            NoteType.G -> Pair(GClefNoteLine.G, null)
            NoteType.G_SHARP -> Pair(GClefNoteLine.G, Accidental.SHARP)
        }

    }

}
