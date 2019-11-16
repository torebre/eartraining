package com.kjipo.scoregenerator

import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TICKS_PER_QUARTER_NOTE
import kotlin.random.Random

class SimpleSequenceGenerator {


    companion object {
        fun createSequence(): SimpleNoteSequence {
            var timeRemaining = 4 * TICKS_PER_QUARTER_NOTE

            var currentNote = NoteType.values()[Random.nextInt(NoteType.values().size)]
            var currentOctave = 5

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
                        currentNote = NoteType.values()[(NoteType.values().size + currentNote.ordinal - 1) % NoteType.values().size]
                    }
                }

                var duration = getDuration()
                if (timeRemaining - duration.ticks < 0) {
                    duration = ticksToDuration(timeRemaining)
                    timeRemaining = 0
                }
                else {
                    timeRemaining -= duration.ticks
                }

                result.add(NoteSequenceElement.NoteElement(currentNote, currentOctave, duration))

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


    }


}