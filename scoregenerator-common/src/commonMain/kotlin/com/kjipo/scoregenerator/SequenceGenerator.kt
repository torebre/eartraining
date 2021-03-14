package com.kjipo.scoregenerator

import com.kjipo.handler.*
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import mu.KotlinLogging


/**
 * Stores a sequence of pitches, and wraps a score handler that can create a score based on the pitch sequence.
 */
class SequenceGenerator : ScoreHandlerInterface {
    var scoreHandler = ScoreHandlerSplit()
    val pitchSequence = mutableListOf<Pitch>()
    val actionSequence = mutableListOf<Action>()
    val noteSequence = mutableListOf<NoteSequenceElement>()

    private val logger = KotlinLogging.logger {}

    fun loadSimpleNoteSequence(simpleNoteSequence: SimpleNoteSequence) {
        scoreHandler.clear()
        noteSequence.clear()
        noteSequence.addAll(simpleNoteSequence.elements)

        logger.debug { "Number of elements in sequence ${simpleNoteSequence.elements.size}" }

        for (element in simpleNoteSequence.elements) {
            when (element) {
                is NoteSequenceElement.RestElement -> {
                    scoreHandler.insertRest(element.duration)
                }
                is NoteSequenceElement.NoteElement -> {
                    scoreHandler.insertNote(element.duration, element.octave, element.note)
                }
                is NoteSequenceElement.MultipleNotesElement -> {
                    scoreHandler.insertChord(element.duration, element.elements)
                }
            }
        }
        computePitchSequence()
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
                val index = pitchSequence.indexOf(pitch)
                if (index != -1) {
                    scoreHandler.findNoteType(id)?.let {
                        pitchSequence[index].pitch += if (up) {
                            1
                        } else {
                            -1
                        }
                        scoreHandler.moveNoteOneStep(id, up)
                    }
                }
            }
        computePitchSequence()
    }


    override fun updateDuration(id: String, keyPressed: Int) {
        pitchSequence
            .find { it.id == id }?.let { pitch ->
                pitch.duration = ScoreHandlerUtilities.getDuration(keyPressed)
                computeOnOffPitches()
            }
        scoreHandler.updateDuration(id, keyPressed)
        computePitchSequence()
    }


    override fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    override fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) =
        scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        val idInsertedNote = scoreHandler.insertNote(activeElement, keyPressed)?.let { idInsertedNote ->
//            pitchSequence
//                .find { it.id == activeElement }?.let { pitch ->
//                    scoreHandler.findScoreHandlerElement(idInsertedNote)?.let {
//                        when (it) {
//                            is NoteOrRest -> {
//                                if (it.isNote) {
//                                    pitchSequence.add(
//                                        pitchSequence.indexOf(pitch) + 1,
//                                        Pitch(
//                                            idInsertedNote,
//                                            0,
//                                            0,
//                                            ScoreHandlerUtilities.getPitch(it.noteType, it.octave),
//                                            it.duration
//                                        )
//                                    )
//                                }
//                            }
//                            // TODO Need to support note groups at this level?
//
//                        }
//                    }
//                }
//            computeOnOffPitches()
            computePitchSequence()
            idInsertedNote
        }

        return idInsertedNote
    }

    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int) =
        scoreHandler.switchBetweenNoteAndRest(idOfElementToReplace, keyPressed).also {
            computePitchSequence()
        }

    private fun computePitchSequence() {
        var timeCounter = 0
        pitchSequence.clear()
        actionSequence.clear()

        for (scoreHandlerElement in scoreHandler.getScoreHandlerElements()) {
            when (scoreHandlerElement) {
                is NoteOrRest -> {
                    val duration = scoreHandlerElement.duration
                    val id = scoreHandlerElement.id
                    val noteType = scoreHandlerElement.noteType
                    val octave = scoreHandlerElement.octave
                    val isNote = scoreHandlerElement.isNote

                    val durationInMilliseconds =
                        ScoreHandlerUtilities.getDurationInMilliseconds(duration)
                    handleNoteOrRest(timeCounter, durationInMilliseconds, isNote, noteType, octave, id, duration)

                    timeCounter += durationInMilliseconds
                }
                is NoteGroup -> {
                    timeCounter = handleMultipleNotesElement(timeCounter, scoreHandlerElement)
                }
            }
        }
    }

    private fun handleNoteOrRest(
        timeCounter: Int,
        durationInMilliseconds: Int,
        isNote: Boolean,
        noteType: NoteType,
        octave: Int,
        id: String,
        duration: Duration
    ) {
        val pitchOn = timeCounter
        val pitchOff = timeCounter + durationInMilliseconds

        if (isNote) {
            val pitch =
                ScoreHandlerUtilities.getPitch(noteType, octave)

            pitchSequence.add(
                Pitch(
                    id,
                    pitchOn,
                    pitchOff,
                    pitch,
                    duration
                )
            )

            with(actionSequence) {
                add(Action.PitchEvent(pitchOn, listOf(pitch), true))
                add(Action.PitchEvent(pitchOff, listOf(pitch), false))
                add(Action.HighlightEvent(pitchOn, true, setOf(id)))
                add(Action.HighlightEvent(pitchOff, false, setOf(id)))
            }
        } else {
            // This is a rest
            with(actionSequence) {
                add(Action.HighlightEvent(pitchOn, true, setOf(id)))
                add(Action.HighlightEvent(pitchOff, false, setOf(id)))
            }
        }

        actionSequence.sortBy { it.time }
    }

    private fun handleMultipleNotesElement(
        timeCounter: Int,
        noteGroup: NoteGroup
    ): Int {
        val duration = noteGroup.notes.first().duration
        val updatedTimeCounter = timeCounter + ScoreHandlerUtilities.getDurationInMilliseconds(duration)

        val pitches = mutableListOf<Int>()
        for (event in noteGroup.notes) {
            if (duration != event.duration) {
                throw UnsupportedOperationException("Only durations of same length handled for multiple notes elements so far")
            }

            val pitch = ScoreHandlerUtilities.getPitch(event.noteType, event.octave)
            pitches.add(pitch)
        }

        with(actionSequence) {
            add(Action.PitchEvent(timeCounter, pitches, true))
            add(Action.PitchEvent(updatedTimeCounter, pitches, false))
            add(Action.HighlightEvent(timeCounter, true, setOf(noteGroup.id)))
            add(Action.HighlightEvent(updatedTimeCounter, false, setOf(noteGroup.id)))
        }

        return updatedTimeCounter
    }

    fun getActionSequenceScript(): ActionScript {
        val timeEventMap = mutableMapOf<Int, MutableList<Action>>()
        val eventTimes = mutableSetOf<Int>()

        actionSequence.forEach {
            val eventsAtTime = timeEventMap.getOrPut(it.time, { mutableListOf() })
            eventsAtTime.add(it)
            eventTimes.add(it.time)
        }

        var previousEventTime = 0
        return ActionScript(eventTimes.distinct().sorted().mapNotNull {
            val sleepTime = it - previousEventTime

            val result = Pair(sleepTime, timeEventMap[it]!!)
            previousEventTime = it

            result
        }.toList())
    }

    override fun deleteElement(id: String) =
        scoreHandler.deleteElement(id).also {
            computePitchSequence()
        }

    override fun insertNote(keyPressed: Int) =
        scoreHandler.insertNote(keyPressed).also {
            computePitchSequence()
        }

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int) =
        scoreHandler.insertNote(activeElement, duration, pitch)?.also {
            computePitchSequence()
        }

    override fun insertRest(activeElement: String, duration: Duration) =
        scoreHandler.insertRest(activeElement, duration).also {
            computeOnOffPitches()
        }

    override fun addNoteGroup(duration: Duration, pitches: List<ScoreHandlerInterface.GroupNote>) =
        scoreHandler.addNoteGroup(duration, pitches)

    override fun getHighlightElementsMap() = scoreHandler.getHighlightElementsMap()

    override fun applyOperation(operation: ScoreOperation): String? {
        TODO("Not yet implemented")
    }



}