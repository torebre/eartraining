package com.kjipo.handler

import com.kjipo.score.Accidental
import com.kjipo.score.Duration
import com.kjipo.score.NoteType

sealed class ScoreHandlerElement {
    abstract val id: String

}

data class NoteOrRest(
    override val id: String,
    var duration: Duration,
    var isNote: Boolean,
    var octave: Int,
    var noteType: NoteType
//    var accidental: Accidental?
) : ScoreHandlerElement()

data class NoteSymbol(
    val id: String,
    val duration: Duration,
    val octave: Int,
    val noteType: NoteType
)

data class NoteGroup(override val id: String, val notes: List<NoteSymbol>) : ScoreHandlerElement()
