package com.kjipo.score


sealed class NoteSequenceElement(val id: String, val duration: Duration, val properties: Map<String, String>) {


    class MultipleNotesElement(
        id: String,
        val elements: Collection<NoteElement>,
        duration: Duration,
        properties: Map<String, String>
    ) :
        NoteSequenceElement(id, duration, properties) {

        override fun toString(): String {
            return "MultipleNotesElement(id=$id, elements=$elements) ${super.toString()}"
        }
    }

    class NoteElement(
        id: String,
        val note: NoteType,
        val octave: Int,
        duration: Duration,
        properties: Map<String, String>
    ) : NoteSequenceElement(id, duration, properties) {

        override fun toString(): String {
            return "NoteElement(id=$id, note=$note, octave=$octave) ${super.toString()}"
        }
    }

    class RestElement(
        id: String,
        duration: Duration,
        properties: Map<String, String>
    ) : NoteSequenceElement(id, duration, properties) {

        override fun toString(): String {
            return "RestElement() ${super.toString()}"
        }
    }

    override fun toString(): String {
        return "NoteSequenceElement(duration=$duration)"
    }

}
