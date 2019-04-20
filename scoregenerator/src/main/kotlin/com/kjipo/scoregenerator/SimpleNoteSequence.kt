package com.kjipo.scoregenerator

import com.kjipo.score.Duration
import com.kjipo.score.NoteType


data class SimpleNoteSequence(val elements: List<NoteSequenceElement>) {


    fun transformToPitchSequence(): List<Pitch> {
        var timeCounter = 0
        val pitchSequence = mutableListOf<Pitch>()
        var idCounter = 0

        for (element in elements) {
            val durationInMilliseconds = Utilities.getDurationInMilliseconds(element.duration)

            if (element is NoteSequenceElement.NoteElement) {
                pitchSequence.add(Pitch(idCounter++.toString(), timeCounter, timeCounter + durationInMilliseconds, Utilities.getPitch(element.note, element.octave), element.duration))
            }
            timeCounter += durationInMilliseconds
        }

        return pitchSequence
    }


}


sealed class NoteSequenceElement(val duration: Duration) {

    class NoteElement(val note: NoteType,
                      val octave: Int,
                      duration: Duration) : NoteSequenceElement(duration)

    class RestElement(duration: Duration) : NoteSequenceElement(duration)


}