package com.kjipo.score


sealed class NoteSequenceElement(val id: String, val duration: Duration) {

    class MultipleNotesElement(id: String, val elements: Collection<NoteElement>, duration: Duration) :
        NoteSequenceElement(id, duration) {

        override fun toString(): String {
            return "MultipleNotesElement(elements=$elements) ${super.toString()}"
        }
    }

    class NoteElement(
        id: String,
        val note: NoteType,
        val octave: Int,
        duration: Duration
    ) : NoteSequenceElement(id, duration) {

        override fun toString(): String {
            return "NoteElement(note=$note, octave=$octave) ${super.toString()}"
        }
    }

    class RestElement(id: String, duration: Duration) : NoteSequenceElement(id, duration) {

        override fun toString(): String {
            return "RestElement() ${super.toString()}"
        }
    }

    override fun toString(): String {
        return "NoteSequenceElement(duration=$duration)"
    }

}
