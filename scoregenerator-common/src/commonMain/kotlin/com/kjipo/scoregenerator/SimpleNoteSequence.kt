package com.kjipo.scoregenerator

import com.kjipo.handler.ScoreHandlerUtilities.getDurationInMilliseconds
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.score.Duration
import com.kjipo.score.NoteType


data class SimpleNoteSequence(val elements: List<NoteSequenceElement>) {


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
            timeCounter += durationInMilliseconds
        }

        return pitchSequence
    }


}


sealed class NoteSequenceElement(val duration: Duration) {

    class MultipleNotesElement(val elements: Collection<NoteElement>, duration: Duration) :
        NoteSequenceElement(duration) {

        override fun toString(): String {
            return "MultipleNotesElement(elements=$elements) ${super.toString()}"
        }
    }

    class NoteElement(
        val note: NoteType,
        val octave: Int,
        duration: Duration
    ) : NoteSequenceElement(duration) {

        override fun toString(): String {
            return "NoteElement(note=$note, octave=$octave) ${super.toString()}"
        }
    }

    class RestElement(duration: Duration) : NoteSequenceElement(duration) {

        override fun toString(): String {
            return "RestElement() ${super.toString()}"
        }
    }

    override fun toString(): String {
        return "NoteSequenceElement(duration=$duration)"
    }

}