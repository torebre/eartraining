package com.kjipo.handler

import com.github.aakira.napier.Napier
import com.kjipo.score.*
import kotlinx.serialization.json.Json
import kotlin.math.truncate

/**
 * Stores a sequence of temporal elements, and can produce a score based on them.
 *
 */
class ScoreHandler : ScoreHandlerInterface {
    private val scoreHandlerElements = mutableListOf<ScoreHandlerElement>()
    private var idCounter = 0
    private val ticksPerBar = 4 * TICKS_PER_QUARTER_NOTE

    private val beams = mutableListOf<BeamGroup>()

    private var trimEndBars = true


    override fun getScoreAsJson() = truncateNumbers(Json.encodeToString(RenderingSequence.serializer(), build()))

    /**
     * It is not necessary to include too many decimal places in the JSON output. This method is a quick fix for truncating the number of decimals in the output.
     */
    private fun truncateNumbers(scoreAsJsonString: String, decimalPlacesToInclude: Int = 4): String {
        val regexp = Regex("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?")
        val matchResults = regexp.findAll(scoreAsJsonString)

        val numbersToReplace = matchResults.map {
            val indexOfSeparator = it.value.indexOf('.')

            if (indexOfSeparator == -1) {
                null
            } else {
                val decimalPoints = it.value.substring(indexOfSeparator + 1)
                if (decimalPoints.length > decimalPlacesToInclude) {
                    var numberAsString = it.value.substring(0, indexOfSeparator + decimalPlacesToInclude + 1)
                    // Remove unnecessary trailing 0 after the decimal point
                    while (numberAsString.endsWith('0')) {
                        numberAsString = numberAsString.substring(0, numberAsString.length - 1)
                    }
                    if (numberAsString.endsWith('.')) {
                        numberAsString = numberAsString.substring(0, numberAsString.length - 1)
                    }
                    if (numberAsString == "-0") {
                        numberAsString = "0"
                    }
                    Pair(it.value, numberAsString)
                } else {
                    null
                }
            }
        }.filterNotNull().toList()

        var scoreWithShortenedNumbers = scoreAsJsonString
        for (numberToReplace in numbersToReplace) {
            scoreWithShortenedNumbers = scoreWithShortenedNumbers.replace(numberToReplace.first, numberToReplace.second)
        }

        return scoreWithShortenedNumbers
    }

    fun clear() {
        idCounter = 0
        scoreHandlerElements.clear()
        beams.clear()
    }

    fun build(): RenderingSequence {
        val scoreSetup = ScoreSetup()

        // This is to make the IDs the same every time the score is rendered. It is needed to make tests for the diff-functionality pass
        NoteElement.noteElementIdCounter = 0
        BarData.barNumber = 0
        BarData.stemCounter = 0
        ExtraBarLinesElement.idCounter = 0


        // TODO Not necessary to have this here. Beams should be computed automatically below
        scoreSetup.beams.addAll(beams)
        var remainingTicksInBar = ticksPerBar
        var currentBar = BarData()
        currentBar.clef = Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)
        val bars = mutableListOf(currentBar)

        Napier.d("Score handler elements: $scoreHandlerElements")

        for (element in scoreHandlerElements) {
            val ticksNeededForElement = element.duration.ticks

            if (remainingTicksInBar == 0) {
                // No more room in bar, start on a new one
                remainingTicksInBar = ticksPerBar
                currentBar = BarData()
                bars.add(currentBar)
            }

            when {
                remainingTicksInBar < ticksNeededForElement -> {
                    val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar)
                    val durationsInNextBar = ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar)

                    val previous = addAndTie(element, durationsInCurrentBar, currentBar, scoreSetup = scoreSetup)
                    remainingTicksInBar += ticksPerBar - ticksNeededForElement
                    currentBar = BarData()
                    bars.add(currentBar)
                    addAndTie(element, durationsInNextBar, currentBar, previous, scoreSetup = scoreSetup)
                }
                else -> {
                    remainingTicksInBar -= ticksNeededForElement
                    val temporalElement = createTemporalElement(element)
                    currentBar.scoreRenderingElements.add(temporalElement)
                }
            }
        }

        Napier.d("Number of bars: ${bars.size}. Remaining ticks in bar: $remainingTicksInBar")

        var lastBarTrimmed = false
        if (trimEndBars && bars.size > 1) {
            val barsBeforeTrimming = bars.size
            trimBars(bars)
            lastBarTrimmed = barsBeforeTrimming != bars.size
        }

        Napier.d("After trimming. Number of bars: ${bars.size}. Remaining ticks in bar: $remainingTicksInBar")

        if (!lastBarTrimmed) {
            fillInLastBar(bars, remainingTicksInBar)
        }

        Napier.d("After fill in. Number of bars: ${bars.size}. Remaining ticks in bar: $remainingTicksInBar")

        bars.forEach {
            scoreSetup.beams.addAll(addBeams(it))
            Napier.d("Bar data: $it")
        }
        scoreSetup.bars.addAll(bars)

        return scoreSetup.build()
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

    private fun addAndTie(element: ScoreHandlerElement, durations: List<Duration>, barData: BarData, previous: ScoreRenderingElement? = null, scoreSetup: ScoreSetup): ScoreRenderingElement? {
        var previousInternal = previous
        for (duration in durations) {
            val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
                NoteElement(element.noteType, element.octave, duration, element.id)
            } else {
                RestElement(duration, element.id)
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

    private fun createTemporalElement(element: ScoreHandlerElement): ScoreRenderingElement {
        return if (element.isNote) {
            NoteElement(element.noteType, element.octave, element.duration, element.id).also {
                if (element.accidental != null) {
                    it.accidental = element.accidental
                }
            }
        } else {
            RestElement(element.duration, element.id)
        }
    }

    private fun fillInLastBar(bars: MutableList<BarData>, ticksRemainingInBar: Int) {
        if (bars.isEmpty() || ticksRemainingInBar == 0) {
            return
        }

        bars.last().let { lastBar ->
            ScoreHandlerUtilities.splitIntoDurations(ticksRemainingInBar).forEach {
                val scoreHandlerElement = ScoreHandlerElement((++idCounter).toString(), it, false, 5, NoteType.C, accidental = null)
                scoreHandlerElements.add(scoreHandlerElement)
                lastBar.scoreRenderingElements.add(RestElement(it, scoreHandlerElement.id))
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
            if (!it.isNote) {
                // Cannot move an element which is not a note
                return@let
            }

            if (up) {
                if (it.noteType == NoteType.H) {
                    it.noteType = NoteType.C
                    ++it.octave
                } else {
                    it.noteType = NoteType.values()[(it.noteType.ordinal + 1) % NoteType.values().size]
                }
            } else {
                if (it.noteType == NoteType.C) {
                    it.noteType = NoteType.H
                    --it.octave
                } else {
                    it.noteType = NoteType.values()[(NoteType.values().size + it.noteType.ordinal - 1) % NoteType.values().size]
                }
            }
        }
    }

    override fun getIdOfFirstSelectableElement(): String? {
        return scoreHandlerElements.firstOrNull()?.let { it.id }
    }

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
            it.duration = ScoreHandlerUtilities.getDuration(keyPressed)
        }
    }

    override fun insertNote(activeElement: String, keyPressed: Int) = insertNote(activeElement, ScoreHandlerUtilities.getDuration(keyPressed))

    fun insertNote(activeElement: String, duration: Duration): String? {
        scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreHandlerElements.indexOf(element) + 1
            scoreHandlerElements.add(insertIndex, ScoreHandlerElement((++idCounter).toString(), duration, true, 5, NoteType.C, accidental = null))
            return scoreHandlerElements[insertIndex].id
        }
        return null
    }

    override fun insertNote(keyPressed: Int) = insertNote(ScoreHandlerUtilities.getDuration(keyPressed))

    fun insertNote(duration: Duration): String {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, true, 5, NoteType.C, accidental = null))
        return scoreHandlerElements.last().id
    }

    fun insertNote(duration: Duration, octave: Int, noteType: NoteType): String {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, true, octave, noteType, accidental = null))
        return scoreHandlerElements.last().id
    }

    fun insertRest(duration: Duration): String {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, false, 5, NoteType.C, accidental = null))
        return scoreHandlerElements.last().id
    }

    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        scoreHandlerElements.find { it.id == idOfElementToReplace }?.let {
            it.isNote = !it.isNote
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
            if (it.isNote) {
                it.noteType
            } else {
                null
            }
        }
    }

    fun findScoreHandlerElement(id: String): ScoreHandlerElement? {
        return scoreHandlerElements.find { it.id == id }
    }

    fun getScoreHandlerElements(): List<ScoreHandlerElement> {
        return scoreHandlerElements
    }

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int): String? {
        scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing


            val insertIndex = scoreHandlerElements.indexOf(element) + 1
            scoreHandlerElements.add(insertIndex, ScoreHandlerElement((++idCounter).toString(), duration, true, 5, NoteType.C, accidental = null))
            return scoreHandlerElements[insertIndex].id
        }
        return null
    }

    override fun insertRest(activeElement: String, duration: Duration): String? {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, false, 5, NoteType.C, accidental = null))
        return scoreHandlerElements.last().id
    }

    override fun toggleExtra(id: String, extra: Accidental) {
        scoreHandlerElements.find { it.id == id }?.let {
            if (it.isNote) {
                if (it.accidental == extra) {
                    it.accidental = null
                } else {
                    it.accidental = extra
                }
            }
        }
    }


}
