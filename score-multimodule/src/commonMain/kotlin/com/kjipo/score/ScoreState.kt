package com.kjipo.score

interface ScoreState {


    fun stemUp(noteId: String) = Stem.UP

    // TODO This only works for C scale and G clef
    fun stemUp(pitch: Int) = if (pitch >= 71) {
        Stem.DOWN
    } else {
        Stem.UP
    }


}