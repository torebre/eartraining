package com.kjipo.scoregenerator

import com.kjipo.score.Duration
import com.kjipo.score.NoteType



object Utilities {

    const val DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE = 1000


    fun getPitch(noteType: NoteType, octave: Int): Int {
        return 12 * octave + when (noteType) {
            NoteType.A -> 9
            NoteType.H -> 11
            NoteType.C -> 0
            NoteType.D -> 2
            NoteType.E -> 4
            NoteType.F -> 5
            NoteType.G -> 7
        }
    }


    fun getDurationInMilliseconds(duration: Duration): Int {
        return when (duration) {
            Duration.HALF -> 2 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            Duration.QUARTER -> DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            Duration.WHOLE -> 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
        }
    }



}