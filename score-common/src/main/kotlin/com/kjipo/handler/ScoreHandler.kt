package com.kjipo.handler

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

    var trimEndBars = true


    override fun getScoreAsJson(): String {
        return JSON.stringify(RenderingSequence.serializer(), build())
    }

    fun build(): RenderingSequence {
        val scoreSetup = ScoreSetup()
        var remainingTicksInBar = ticksPerBar
        var currentBar = BarData()
        currentBar.clef = com.kjipo.score.Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)
        val bars = mutableListOf(currentBar)

        for (element in scoreHandlerElements) {
            // TODO This does not tie notes across bars

            var ticksNeededForElement = element.duration.ticks

            when {
                remainingTicksInBar == 0 -> {
                    remainingTicksInBar = ticksPerBar
                    currentBar = BarData()
                    bars.add(currentBar)
                }
                remainingTicksInBar < ticksNeededForElement -> {
                    val (durationInCurrentBar, durationInNextPar) = ScoreHandlerUtilities.splitDuration(ticksNeededForElement, remainingTicksInBar)

                    val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
                        NoteElement(element.noteType, element.octave, durationInCurrentBar, element.id)
                    } else {
                        RestElement(durationInCurrentBar, element.id)
                    }

                    ticksNeededForElement -= remainingTicksInBar
                    currentBar.scoreRenderingElements.add(scoreRenderingElement)
                    remainingTicksInBar = ticksPerBar
                    currentBar = BarData()


                    if (durationInCurrentBar != Duration.ZERO) {
                        val scoreRenderingElementInNextBar: ScoreRenderingElement = if (element.isNote) {
                            NoteElement(element.noteType, element.octave, durationInNextPar, element.id)
                        } else {
                            RestElement(durationInCurrentBar, element.id)
                        }
                        currentBar.scoreRenderingElements.add(scoreRenderingElementInNextBar)

                        // Add a tie between the part of the note in the current bar and the note in the next bar
                        if(scoreRenderingElementInNextBar is NoteElement) {
                            scoreSetup.ties.add(TiePair(scoreRenderingElement as NoteElement, scoreRenderingElementInNextBar))
                        }

                    }

                    bars.add(currentBar)
                }
                else -> remainingTicksInBar -= ticksNeededForElement
            }

            val duration = ScoreHandlerUtilities.getDurationForTicks(ticksNeededForElement)
            val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
                NoteElement(element.noteType, element.octave, duration, element.id)
            } else {
                RestElement(duration, element.id)
            }

            currentBar.scoreRenderingElements.add(scoreRenderingElement)
        }

        if (trimEndBars) {
            trimBars(bars)
        }
        scoreSetup.bars.addAll(bars)

        return scoreSetup.build()
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

}

