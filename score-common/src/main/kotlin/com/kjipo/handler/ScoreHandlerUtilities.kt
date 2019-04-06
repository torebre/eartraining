package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteElement
import com.kjipo.score.NoteType

object ScoreHandlerUtilities {


    // TODO Only works for the C-scale
    fun determinePitchStep(noteElement: NoteElement, up: Boolean): Int {
        return ScoreHandlerUtilities.determinePitchStep(noteElement, up)
    }

    fun determinePitchStep(noteType: NoteType, up: Boolean): Int {
        return when (noteType) {
            NoteType.A -> if (up) {
                2
            } else {
                -2
            }
            NoteType.H -> if (up) {
                1
            } else {
                -2
            }
            NoteType.C -> if (up) {
                2
            } else {
                -1
            }
            NoteType.D -> if (up) {
                2
            } else {
                -2
            }
            NoteType.E -> if (up) {
                1
            } else {
                -2
            }
            NoteType.F -> if (up) {
                2
            } else {
                -1
            }
            NoteType.G -> if (up) {
                2
            } else {
                -2
            }
        }
    }

    fun getDuration(keyPressed: Int): Duration =
            when (keyPressed) {
                1 -> Duration.QUARTER
                2 -> Duration.HALF
                3 -> Duration.WHOLE
                else -> Duration.QUARTER
            }

}