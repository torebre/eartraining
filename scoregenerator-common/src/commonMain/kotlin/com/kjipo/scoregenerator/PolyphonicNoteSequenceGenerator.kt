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

class PolyphonicNoteSequenceGenerator {
    private val startingOctave = 5
    private val probabilityOfAddingInterval = 0.4
    private val probabilityOfAddingThird = 0.6


    fun createSequence(): SimpleNoteSequence {
        var timeRemaining = 4 * TICKS_PER_QUARTER_NOTE

        var currentNote = NoteType.values()[Random.nextInt(NoteType.values().size)]
        var currentOctave = startingOctave

        val result = mutableListOf<NoteSequenceElement>()

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

            if (Random.nextDouble() < probabilityOfAddingInterval) {
                val currentPitch = getPitch(currentNote, currentOctave)
                val intervalNote = addInterval(currentPitch)
                result.add(
                    NoteSequenceElement.MultipleNotesElement(
                        listOf(
                            NoteSequenceElement.NoteElement(currentNote, currentOctave, duration),
                            NoteSequenceElement.NoteElement(intervalNote.first, intervalNote.second, duration)
                        ), duration
                    )
                )
            } else {
                result.add(NoteSequenceElement.NoteElement(currentNote, currentOctave, duration))
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

        fun transformToSimplePitchEventSequence(simpleNoteSequence: SimpleNoteSequence): List<Pair<Collection<SimplePitchEvent>, Int>> {
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
            return timePitchPoints.distinct().sorted().mapNotNull {
                val sleepTime = it - previousEventTime
                val currentPitchEvent = if (!timePitchEventMap.containsKey(it)) {
                    null
                } else {
                    Pair(timePitchEventMap[it]!!, sleepTime)
                }
                previousEventTime = it
                currentPitchEvent
            }.toList()
        }
    }

}