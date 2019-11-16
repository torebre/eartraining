package com.kjipo.scoregenerator

import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreHandlerInterface
import com.kjipo.handler.ScoreHandlerUtilities
import com.kjipo.score.Accidental
import com.kjipo.score.Duration


/**
 * Stores a sequence of pitches, and wraps a score handler that can create a score based on the pitch sequence.
 */
class SequenceGenerator : ScoreHandlerInterface {
    private var scoreHandler: ScoreHandler = ScoreHandler()
    private val pitchSequence = mutableListOf<Pitch>()


    fun loadSimpleNoteSequence(simpleNoteSequence: SimpleNoteSequence) {
        scoreHandler.clear()
        var timeCounter = 0
        pitchSequence.clear()

        for (element in simpleNoteSequence.elements) {
            val durationInMilliseconds = ScoreHandlerUtilities.getDurationInMilliseconds(element.duration)

            when (element) {
                is NoteSequenceElement.RestElement -> {
                    scoreHandler.insertRest(element.duration)
                }
                is NoteSequenceElement.NoteElement -> {
                    val id = scoreHandler.insertNote(element.duration, element.octave, element.note)
                    pitchSequence.add(Pitch(id, timeCounter, timeCounter + durationInMilliseconds, ScoreHandlerUtilities.getPitch(element.note, element.octave), element.duration))
                }
            }
            timeCounter += durationInMilliseconds
        }
    }

    private fun computeOnOffPitches() {
        var timeCounter = 0
        pitchSequence.forEach {
            it.timeOn = timeCounter
            it.timeOff = timeCounter + ScoreHandlerUtilities.getDurationInMilliseconds(it.duration)
            timeCounter = it.timeOff
        }
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {
        pitchSequence
                .find { it.id == id }?.let { pitch ->

                    // TODO Only works because C major is the only key used so far
                    scoreHandler.moveNoteOneStep(id, up)
                    val index = pitchSequence.indexOf(pitch)
                    if (index != -1) {
                        scoreHandler.findNoteType(id)?.let {
                            val pitchStep = ScoreHandlerUtilities.determinePitchStep(it, up)
                            pitchSequence[index].pitch += pitchStep
                        }
                    }
                }
        scoreHandler.moveNoteOneStep(id, up)
    }


    override fun updateDuration(id: String, keyPressed: Int) {
        pitchSequence
                .find { it.id == id }?.let { pitch ->
                    pitch.duration = ScoreHandlerUtilities.getDuration(keyPressed)
                    computeOnOffPitches()
                }
        scoreHandler.updateDuration(id, keyPressed)
    }


    override fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    override fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        scoreHandler.insertNote(activeElement, keyPressed)?.let { idInsertedNote ->
            pitchSequence
                    .find { it.id == activeElement }?.let { pitch ->
                        scoreHandler.findScoreHandlerElement(idInsertedNote)?.let {
                            if (it.isNote) {
                                pitchSequence.add(pitchSequence.indexOf(pitch) + 1, Pitch(idInsertedNote, 0, 0, ScoreHandlerUtilities.getPitch(it.noteType, it.octave), it.duration))
                            }
                        }
                    }
            computeOnOffPitches()
            return idInsertedNote
        }
        return null
    }

    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        val idOfNewElement = scoreHandler.switchBetweenNoteAndRest(idOfElementToReplace, keyPressed)

        // Compute the whole pitch sequence again to keep it easy
        computePitchSequence()
        return idOfNewElement
    }

    private fun computePitchSequence() {
        var timeCounter = 0
        pitchSequence.clear()

        for (scoreHandlerElement in scoreHandler.getScoreHandlerElements()) {
            val durationInMilliseconds = ScoreHandlerUtilities.getDurationInMilliseconds(scoreHandlerElement.duration)

            if (scoreHandlerElement.isNote) {
                val pitch = ScoreHandlerUtilities.getPitch(scoreHandlerElement.noteType, scoreHandlerElement.octave)
                pitchSequence.add(Pitch(scoreHandlerElement.id, timeCounter, timeCounter + durationInMilliseconds, pitch, scoreHandlerElement.duration))
            }
            timeCounter += durationInMilliseconds
        }
    }

    override fun deleteElement(id: String) {
        scoreHandler.deleteElement(id)
        computePitchSequence()
    }

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int): String? {
        return scoreHandler.insertNote(activeElement, duration, pitch)?.also {
            computePitchSequence()
        }
    }

    override fun insertRest(activeElement: String, duration: Duration): String? {
        return scoreHandler.insertRest(activeElement, duration).also {
            computeOnOffPitches()
        }
    }

    override fun toggleExtra(id: String, extra: Accidental) {
        scoreHandler.toggleExtra(id, extra)
        computeOnOffPitches()
    }


}