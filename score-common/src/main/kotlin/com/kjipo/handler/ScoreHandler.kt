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


    override fun getScoreAsJson(): String {
        val scoreSetup = ScoreSetup()
        var remainingTicksInBar = ticksPerBar
        var currentBar = BarData()
        currentBar.clef = com.kjipo.score.Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)

        scoreSetup.bars.add(currentBar)

        for (element in scoreHandlerElements) {
            // TODO For testing making the simplifying assumption that the notes fit exactly within a bar
            if (remainingTicksInBar == 0) {
                remainingTicksInBar = ticksPerBar
                currentBar = BarData()
                scoreSetup.bars.add(currentBar)
            }

            remainingTicksInBar -= element.duration.ticks
            val scoreRenderingElement: ScoreRenderingElement = if (element.isNote) {
                NoteElement(element.noteType, element.octave, element.duration, element.id)
            } else {
                RestElement(element.duration, element.id)
            }

            currentBar.scoreRenderingElements.add(scoreRenderingElement)
            scoreSetup.noteElements.add(scoreRenderingElement as TemporalElement)
        }

        return JSON.stringify(RenderingSequence.serializer(), scoreSetup.build())
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
        return scoreHandlerElements.find { it.id == activeElement }?.let {
            scoreHandlerElements.filter { temporalElement ->
                temporalElement.id == activeElement
            }.map { it ->
                val index = scoreHandlerElements.indexOf(it)
                if (lookLeft) {
                    if (index == 0) {
                        0
                    } else {
                        index - 1
                    }
                } else {
                    if (index == scoreHandlerElements.lastIndex) {
                        scoreHandlerElements.lastIndex
                    } else {
                        index + 1
                    }
                }
            }
                    .map { scoreHandlerElements[it].id }.firstOrNull()
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

