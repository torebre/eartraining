package com.kjipo.handler

import com.github.aakira.napier.Napier
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.score.*
import kotlinx.serialization.json.JSON

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

    internal var scoreSetup = ScoreSetup()

    override fun getScoreAsJson(): String {
        return JSON.stringify(RenderingSequence.serializer(), build())
    }

    fun build(): RenderingSequence {
        scoreSetup = ScoreSetup()
        scoreSetup.beams.addAll(beams)
        var remainingTicksInBar = ticksPerBar
        var currentBar = BarData()
        currentBar.clef = Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)
        val bars = mutableListOf(currentBar)

        Napier.d("Score handler elements: ${scoreHandlerElements}")

        for (element in scoreHandlerElements) {
            var ticksNeededForElement = element.duration.ticks

            if (remainingTicksInBar == 0) {
                // No more room in bar, start on a new one
                remainingTicksInBar = ticksPerBar
                currentBar = BarData()
                bars.add(currentBar)
            }

            when {
                remainingTicksInBar < ticksNeededForElement -> {
                    val durationsInCurrentbar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar)
                    val durationsInNextBar = ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar)

                    var previous = addAndTie(element, durationsInCurrentbar, currentBar)
                    remainingTicksInBar += ticksPerBar - ticksNeededForElement
                    currentBar = BarData()
                    bars.add(currentBar)
                    addAndTie(element, durationsInNextBar, currentBar, previous)
                }
                else -> {
                    remainingTicksInBar -= ticksNeededForElement
                    addElement(element, currentBar)
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

        bars.forEach { println(it) }

        scoreSetup.bars.addAll(bars)

        return scoreSetup.build()
    }


    private fun addAndTie(element: ScoreHandlerElement, durations: List<Duration>, barData: BarData, previous: ScoreRenderingElement? = null): ScoreRenderingElement? {
        var previousInternal = previous
        for (duration in durations) {
            val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
                NoteElement(element.noteType, element.octave, duration, element.id).also {
                    it.stem = stemUp(getPitch(element.noteType, element.octave))
                }
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


    private fun stemUp(pitch: Int): Stem {
        // TODO This only works for C scale and G clef
        return if (pitch >= 71) {
            Stem.DOWN
        } else {
            Stem.UP
        }
    }


    private fun addElement(element: ScoreHandlerElement, currentBar: BarData) {
        val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
            NoteElement(element.noteType, element.octave, element.duration, element.id).also {
                it.stem = stemUp(getPitch(element.noteType, element.octave))
            }
        } else {
            RestElement(element.duration, element.id)
        }
        currentBar.scoreRenderingElements.add(scoreRenderingElement)
    }

    private fun fillInLastBar(bars: MutableList<BarData>, ticksRemainingInBar: Int) {
        if (bars.isEmpty() || ticksRemainingInBar == 0) {
            return
        }

        bars.last().let { lastBar ->
            ScoreHandlerUtilities.splitIntoDurations(ticksRemainingInBar).forEach {
                val scoreHandlerElement = ScoreHandlerElement((++idCounter).toString(), it, false, 5, NoteType.C)
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

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        return insertNote(activeElement, ScoreHandlerUtilities.getDuration(keyPressed))
    }

    fun insertNote(activeElement: String, duration: Duration): String? {
        scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreHandlerElements.indexOf(element) + 1
            scoreHandlerElements.add(insertIndex, ScoreHandlerElement((++idCounter).toString(), duration, true, 5, NoteType.C))
            return scoreHandlerElements[insertIndex].id
        }
        return null
    }

    fun insertNote(duration: Duration): String {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, true, 5, NoteType.C))
        return scoreHandlerElements.last().id
    }

    fun insertNote(duration: Duration, octave: Int, noteType: NoteType): String {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, true, octave, noteType))
        return scoreHandlerElements.last().id
    }

    fun insertRest(duration: Duration): String {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, false, 5, NoteType.C))
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
            scoreHandlerElements.add(insertIndex, ScoreHandlerElement((++idCounter).toString(), duration, true, 5, NoteType.C))
            return scoreHandlerElements[insertIndex].id
        }
        return null
    }

    override fun insertRest(activeElement: String, duration: Duration): String? {
        scoreHandlerElements.add(ScoreHandlerElement((++idCounter).toString(), duration, false, 5, NoteType.C))
        return scoreHandlerElements.last().id
    }

}
