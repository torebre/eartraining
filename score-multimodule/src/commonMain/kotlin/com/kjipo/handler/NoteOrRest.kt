package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType

sealed class ScoreHandlerElement {
    abstract val id: String
    abstract val properties: Map<String, String>
}

data class NoteOrRest(
    override val id: String,
    var duration: Duration,
    var isNote: Boolean,
    var octave: Int,
    var noteType: NoteType,
    override val properties: Map<String, String> = emptyMap()
) : ScoreHandlerElement()


data class NoteGroup(
    override val id: String,
    val notes: List<NoteSymbol>,
    override val properties: Map<String, String> = emptyMap()
) : ScoreHandlerElement()


data class NoteSymbol(
    val id: String,
    val duration: Duration,
    val octave: Int,
    val noteType: NoteType,
)
