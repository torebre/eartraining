package com.kjipo.handler

import com.kjipo.score.Accidental
import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.Stem


/**
 * Represents a symbol that should be rendered. It does not contain
 * information about how it should be rendered.
 */
sealed class ScoreHandlerElement {
    abstract val id: String
    abstract val properties: Map<String, String>
}

data class Note(
    override val id: String,
    val duration: Duration,
    val octave: Int,
    val noteType: NoteType,
    override val properties: Map<String, String> = emptyMap(),
    val stem: Stem = Stem.NONE,
    val accidental: Accidental? = null
) : ScoreHandlerElement(), IsNote

data class Rest(
    override val id: String,
    val duration: Duration,
    override val properties: Map<String, String> = emptyMap()
) :
    ScoreHandlerElement()


data class NoteGroup(
    override val id: String,
    val notes: List<NoteSymbol>,
    override val properties: Map<String, String> = emptyMap(),
    val stem: Stem = Stem.NONE
) : ScoreHandlerElement(), IsNote


data class NoteSymbol(
    val id: String,
    val duration: Duration,
    val octave: Int,
    val noteType: NoteType,
)


interface IsNote {
    val id: String
}