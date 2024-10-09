package com.kjipo.scoregenerator


sealed class Action(val time: Int) {

    class PitchEvent(time: Int, val pitches: List<Int>, val noteOn: Boolean) : Action(time) {
        override fun toString(): String {
            return "PitchEvent(pitches=$pitches, noteOn=$noteOn, time=$time)"
        }
    }

    class HighlightEvent(time: Int, val highlightOn: Boolean, val ids: Collection<String>) : Action(time) {
        override fun toString(): String {
            return "HighlightEvent(highlightOn=$highlightOn, ids=$ids, time=$time)"
        }
    }

}