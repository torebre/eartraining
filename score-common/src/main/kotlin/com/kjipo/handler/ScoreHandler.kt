package com.kjipo.handler

import com.kjipo.score.*


class ScoreHandler(init: SCORE.() -> Unit) {
    private val scoreBuilder = ScoreBuilderImpl()
    var currentScore: RenderingSequence

    init {
        currentScore = scoreBuilder.score(init)
    }

    fun moveNoteOneStep(id: String, up: Boolean) {
        scoreBuilder.findNote(id)?.let {
            if (up) {
                if (it.note == NoteType.H) {
                    it.note = NoteType.C
                    ++it.octave
                } else {
                    it.note = NoteType.values()[(it.note.ordinal + 1) % NoteType.values().size]
                }
            } else {
                if (it.note == NoteType.C) {
                    it.note = NoteType.H
                    --it.octave
                } else {
                    it.note = NoteType.values()[(NoteType.values().size + it.note.ordinal - 1) % NoteType.values().size]
                }
            }
        }
        currentScore = scoreBuilder.build()
    }

    internal fun findNote(id: String): NoteElement? {
        return scoreBuilder.findNote(id)
    }

}

