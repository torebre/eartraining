package com.kjipo.scoregenerator

import com.kjipo.handler.ScoreHandlerUtilities.getDurationInMilliseconds
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.handler.ScoreHandlerUtilities.pitchToNoteAndOctave
import com.kjipo.midi.SimplePitchEvent
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.score.TICKS_PER_QUARTER_NOTE
import kotlin.random.Random


class PolyphonicNoteSequenceGenerator(
    private val startingOctave: Int = 5,
    private val probabilityOfAddingInterval: Double = 0.4,
    private val probabilityOfAddingThird: Double = 0.6
) {

    fun createSequence(allowMultipleNotesAtSameTime: Boolean = true): SimpleNoteSequence {
        var timeRemaining = 4 * TICKS_PER_QUARTER_NOTE

        var currentNote = NoteType.values()[Random.nextInt(NoteType.values().size)]
        var currentOctave = startingOctave

        val result = mutableListOf<NoteSequenceElement>()

        var idCounter = 0

        while (true) {
            val stepUp = Random.nextBoolean()

            if (stepUp) {
                if (currentNote == NoteType.H) {
                    currentNote = NoteType.C
                    ++currentOctave
                } else {
                    currentNote = NoteType.values()[(currentNote.ordinal + 1) % NoteType.values().size]
                }
            } else {
                if (currentNote == NoteType.C) {
                    currentNote = NoteType.H
                    --currentOctave
                } else {
                    currentNote =
                        NoteType.values()[(NoteType.values().size + currentNote.ordinal - 1) % NoteType.values().size]
                }
            }

            var duration = getDuration()
            if (timeRemaining - duration.ticks < 0) {
                duration = ticksToDuration(timeRemaining)
                timeRemaining = 0
            } else {
                timeRemaining -= duration.ticks
            }

            if (allowMultipleNotesAtSameTime && Random.nextDouble() < probabilityOfAddingInterval) {
                val currentPitch = getPitch(currentNote, currentOctave)
                val intervalNote = addInterval(currentPitch)
                val multipleNotesElementId = (++idCounter).toString()
                val noteElementId1 = (++idCounter).toString()
                val noteElementId2 = (++idCounter).toString()

                // The ELEMENT_ID property for each of the note in the note group
                // should be set to be the same as for the multiple note element
                // to make the highlighting work
                result.add(
                    NoteSequenceElement.MultipleNotesElement(
                        multipleNotesElementId,
                        listOf(
                            NoteSequenceElement.NoteElement(
                                noteElementId1,
                                currentNote,
                                currentOctave,
                                duration,
                                mapOf(Pair(ELEMENT_ID, multipleNotesElementId))
                            ),
                            NoteSequenceElement.NoteElement(
                                noteElementId2,
                                intervalNote.first,
                                intervalNote.second,
                                duration,
                                mapOf(Pair(ELEMENT_ID, multipleNotesElementId))
                            )
                        ), duration,
                        mapOf(Pair(ELEMENT_ID, multipleNotesElementId))
                    )
                )
            } else {
                val noteElementId = (++idCounter).toString()
                result.add(
                    NoteSequenceElement.NoteElement(
                        noteElementId,
                        currentNote,
                        currentOctave,
                        duration,
                        mapOf(Pair(ELEMENT_ID, noteElementId))
                    )
                )
            }

            if (timeRemaining == 0) {
                break
            }

        }

        return SimpleNoteSequence(result)
    }


    private fun ticksToDuration(ticks: Int): Duration {
        return Duration.values().first { it.ticks == ticks }
    }


    private fun getDuration(): Duration {
        return if (Random.nextDouble() < 0.3) {
            Duration.HALF
        } else {
            Duration.QUARTER
        }
    }


    /**
     * For now only add a third or a fifth and do not think about the key
     */
    private fun addInterval(pitch: Int) =
        if (Random.nextDouble() < probabilityOfAddingThird) {
            pitchToNoteAndOctave(pitch + 4)
        } else {
            pitchToNoteAndOctave(pitch + 7)
        }


    companion object {

        fun transformToSimplePitchEventSequence(simpleNoteSequence: SimpleNoteSequence): PolyphonicPitchScript {
            var timeCounter = 0
            val timePitchPoints = mutableListOf<Int>()
            val timePitchEventMap = mutableMapOf<Int, MutableCollection<SimplePitchEvent>>()
            var idAsInteger = 0

            for (noteEvent in simpleNoteSequence.elements) {
                when (noteEvent) {
                    is NoteSequenceElement.NoteElement -> {
                        val pitch = getPitch(noteEvent.note, noteEvent.octave)

                        // Compute on-time for pitch
                        if (!timePitchEventMap.containsKey(timeCounter)) {
                            timePitchEventMap[timeCounter] =
                                mutableListOf(SimplePitchEvent(idAsInteger.toString(), true, pitch))
                        } else {
                            timePitchEventMap[timeCounter]?.add(SimplePitchEvent(idAsInteger.toString(), true, pitch))
                        }

                        timePitchPoints.add(timeCounter)
                        timeCounter += getDurationInMilliseconds(noteEvent.duration)
                        timePitchPoints.add(timeCounter)

                        // Compute off-time for pitch
                        if (!timePitchEventMap.containsKey(timeCounter)) {
                            timePitchEventMap[timeCounter] =
                                mutableListOf(SimplePitchEvent(idAsInteger.toString(), false, pitch))
                        } else {
                            timePitchEventMap[timeCounter]?.add(SimplePitchEvent(idAsInteger.toString(), false, pitch))
                        }
                        idAsInteger++
                    }

                    is NoteSequenceElement.MultipleNotesElement -> {
                        val duration = noteEvent.elements.first().duration
                        val noteOn = timeCounter
                        timePitchPoints.add(noteOn)
                        timeCounter += getDurationInMilliseconds(duration)
                        val noteOff = timeCounter
                        timePitchPoints.add(noteOff)

                        for (event in noteEvent.elements) {
                            if (duration != event.duration) {
                                throw UnsupportedOperationException("Only durations of same length handled for multiple notes elements so far")
                            }

                            val pitch = getPitch(event.note, event.octave)

                            // Compute on-time for pitch
                            if (!timePitchEventMap.containsKey(noteOn)) {
                                timePitchEventMap[noteOn] =
                                    mutableListOf(SimplePitchEvent(idAsInteger.toString(), true, pitch))
                            } else {
                                timePitchEventMap[noteOn]?.add(
                                    SimplePitchEvent(
                                        idAsInteger.toString(),
                                        true,
                                        pitch
                                    )
                                )
                            }

                            // Compute off-time for pitch
                            if (!timePitchEventMap.containsKey(noteOff)) {
                                timePitchEventMap[noteOff] =
                                    mutableListOf(SimplePitchEvent(idAsInteger.toString(), false, pitch))
                            } else {
                                timePitchEventMap[noteOff]?.add(
                                    SimplePitchEvent(
                                        idAsInteger.toString(),
                                        false,
                                        pitch
                                    )
                                )
                            }
                            idAsInteger++
                        }

                    }

                    is NoteSequenceElement.RestElement -> {
                        timeCounter += getDurationInMilliseconds(noteEvent.duration)
                    }
                }
            }

            var previousEventTime = 0
            return PolyphonicPitchScript(timePitchPoints.distinct().sorted().mapNotNull {
                val sleepTime = it - previousEventTime
                val currentPitchEvent = if (!timePitchEventMap.containsKey(it)) {
                    null
                } else {
                    Pair(timePitchEventMap[it]!!, sleepTime)
                }
                previousEventTime = it
                currentPitchEvent
            }.toList())
        }
    }

}