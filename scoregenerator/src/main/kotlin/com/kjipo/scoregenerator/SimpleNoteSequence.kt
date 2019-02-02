package com.kjipo.scoregenerator

import com.kjipo.score.Duration
import com.kjipo.score.NoteType


data class SimpleNoteSequence(val elements: List<NoteSequenceElement>)


sealed class NoteSequenceElement(val duration: Duration) {

    class NoteElement(val note: NoteType,
                      val octave: Int,
                      duration: Duration) : NoteSequenceElement(duration)

    class RestElement(duration: Duration) : NoteSequenceElement(duration)


}