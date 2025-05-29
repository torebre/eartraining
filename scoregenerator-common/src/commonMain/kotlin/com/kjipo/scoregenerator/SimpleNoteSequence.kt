package com.kjipo.scoregenerator

import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandlerUtilities.getDurationInMilliseconds
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.handler.PitchSequenceOperation
import com.kjipo.score.NoteSequenceElement


class SimpleNoteSequence(val elements: List<NoteSequenceElement>) {


    fun transformToPitchSequence(): List<Pitch> {
        var timeCounter = 0
        val pitchSequence = mutableListOf<Pitch>()
        var idCounter = 0

        for (element in elements) {
            val durationInMilliseconds = getDurationInMilliseconds(element.duration)

            if (element is NoteSequenceElement.NoteElement) {
                pitchSequence.add(
                    Pitch(
                        idCounter++.toString(),
                        timeCounter,
                        timeCounter + durationInMilliseconds,
                        getPitch(element.note, element.octave),
                        element.duration
                    )
                )
            }
            else if(element is NoteSequenceElement.MultipleNotesElement) {
                throw UnsupportedOperationException("Handling multiple note elements is not implemented")
            }

            timeCounter += durationInMilliseconds
        }

        return pitchSequence
    }

    override fun toString(): String {
        return "SimpleNoteSequence(elements=$elements)"
    }


    companion object {

        fun applyOperationToNoteSequence(operation: PitchSequenceOperation, noteSequence: SimpleNoteSequence) {
            when (operation) {
                is InsertNote -> {
                    // TODO
                }

                else -> {
                    // TODO
                }
            }


        }
    }

}


