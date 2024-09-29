package com.kjipo.scoregenerator

import com.kjipo.handler.ScoreHandlerUtilities
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType


fun computePitchSequence(
    noteSequence: List<NoteSequenceElement>,
): Pair<MutableList<Pitch>, MutableList<Action>> {
    var timeCounter = 0
    val newPitchSequence = mutableListOf<Pitch>()
    val newActionSequence = mutableListOf<Action>()

    for (noteSequenceElement in noteSequence) {
        when (noteSequenceElement) {
            is NoteSequenceElement.NoteElement -> {
                val durationInMilliseconds =
                    ScoreHandlerUtilities.getDurationInMilliseconds(noteSequenceElement.duration)
                handleNoteOrRest(
                    timeCounter,
                    durationInMilliseconds,
                    true,
                    noteSequenceElement.note,
                    noteSequenceElement.octave,
                    noteSequenceElement.id,
                    noteSequenceElement.duration,
                    newActionSequence,
                    newPitchSequence
                )

                timeCounter += durationInMilliseconds
            }

            is NoteSequenceElement.RestElement -> {
                val durationInMilliseconds =
                    ScoreHandlerUtilities.getDurationInMilliseconds(noteSequenceElement.duration)
                handleNoteOrRest(
                    timeCounter,
                    durationInMilliseconds,
                    false,
                    NoteType.C,
                    5,
                    noteSequenceElement.id,
                    noteSequenceElement.duration,
                    newActionSequence,
                    newPitchSequence
                )
                timeCounter += durationInMilliseconds
            }

            is NoteSequenceElement.MultipleNotesElement -> {
                timeCounter = handleMultipleNotesElement(
                    timeCounter,
                    noteSequenceElement,
                    newActionSequence,
                    newPitchSequence
                )
            }
        }
    }

    return Pair(newPitchSequence, newActionSequence)
}

private fun handleNoteOrRest(
    timeCounter: Int,
    durationInMilliseconds: Int,
    isNote: Boolean,
    noteType: NoteType,
    octave: Int,
    id: String,
    duration: Duration,
    actionSequence: MutableList<Action>,
    pitchSequence: MutableList<Pitch>
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
    noteGroup: NoteSequenceElement.MultipleNotesElement,
    newActionSequence: MutableList<Action>,
    newPitchSequence: MutableList<Pitch>
): Int {
    val duration = noteGroup.elements.first().duration
    val updatedTimeCounter = timeCounter + ScoreHandlerUtilities.getDurationInMilliseconds(duration)

    val pitches = mutableListOf<Int>()
    for (event in noteGroup.elements) {
        if (duration != event.duration) {
            throw UnsupportedOperationException("Only durations of same length handled for multiple notes elements so far")
        }

        val pitch = ScoreHandlerUtilities.getPitch(event.note, event.octave)
        pitches.add(pitch)

        newPitchSequence.add(
            Pitch(
                event.id,
                timeCounter,
                updatedTimeCounter,
                pitch,
                duration
            )
        )
    }

    with(newActionSequence) {
        add(Action.PitchEvent(timeCounter, pitches, true))
        add(Action.PitchEvent(updatedTimeCounter, pitches, false))
        add(Action.HighlightEvent(timeCounter, true, setOf(noteGroup.id)))
        add(Action.HighlightEvent(updatedTimeCounter, false, setOf(noteGroup.id)))
    }

    return updatedTimeCounter
}


fun actionScript(actionSequence: List<Action>): ActionScript {
    val timeEventMap = mutableMapOf<Int, MutableList<Action>>()
    val eventTimes = mutableSetOf<Int>()

    // logger.debug { "Action sequence pitch length: ${actionSequence.filter { it is Action.PitchEvent }.count()}" }

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
