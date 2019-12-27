package com.kjipo.scoregenerator


sealed class Action(val time: Int) {

    class PitchEvent(time: Int, val pitch: Int, val noteOn: Boolean): Action(time)

    class HighlightEvent(time: Int, val highlightOn: Boolean, val ids: Collection<String>): Action(time)


}