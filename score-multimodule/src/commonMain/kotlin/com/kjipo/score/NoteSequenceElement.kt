package com.kjipo.score


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
