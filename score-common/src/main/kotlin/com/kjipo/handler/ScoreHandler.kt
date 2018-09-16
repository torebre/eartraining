package com.kjipo.handler

import com.kjipo.score.*
import kotlinx.serialization.json.JSON


class ScoreHandler private constructor(init: (SCORE.() -> Unit)?, scoreBuilder: ScoreBuilderImpl?) : ScoreHandlerInterface {
    var scoreBuilder = ScoreBuilderImpl()
    var currentScore: RenderingSequence

    constructor(scoreBuilder: ScoreBuilderImpl) : this(null, scoreBuilder)

    constructor(init: SCORE.() -> Unit) : this(init, null)

    init {
        this.scoreBuilder = if (scoreBuilder == null) {
            ScoreBuilderImpl()
        } else {
            scoreBuilder
        }
        currentScore = if (init == null) {
            this.scoreBuilder.build()
        } else {
            this.scoreBuilder.score(init)
        }
    }

    constructor() : this({})

    fun updateScore() {
        currentScore = scoreBuilder.build()
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {
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

    override fun getScoreAsJson(): String {
        return JSON.stringify(currentScore)
    }

    override fun getIdOfFirstSelectableElement() = scoreBuilder.noteElements.map { it.id }.firstOrNull()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String? {
        return scoreBuilder.noteElements.find { it.id.equals(activeElement) }?.let { noteElement ->
            scoreBuilder.noteElements.filter { temporalElement ->
                temporalElement.id.equals(activeElement)
            }.map { it ->
                val index = scoreBuilder.noteElements.indexOf(it)
                if (lookLeft) {
                    if (index == 0) {
                        0
                    } else {
                        index - 1
                    }
                } else {
                    if (index == scoreBuilder.noteElements.lastIndex) {
                        scoreBuilder.noteElements.lastIndex
                    } else {
                        index + 1
                    }
                }
            }
                    .map { scoreBuilder.noteElements[it].id }.firstOrNull()
        }
    }

    override fun updateDuration(id: String, keyPressed: Int) {
        scoreBuilder.noteElements.find {
            it.id.equals(id)
        }?.let {
            when (keyPressed) {
                1 -> it.duration = Duration.QUARTER
                2 -> it.duration = Duration.HALF
                3 -> it.duration = Duration.WHOLE
            }
            currentScore = scoreBuilder.build()
        }
    }

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        scoreBuilder.noteElements.find { it.id == activeElement }?.let { element ->
            val duration = when (keyPressed) {
                1 -> Duration.QUARTER
                2 -> Duration.HALF
                3 -> Duration.WHOLE
                else -> Duration.QUARTER
            }

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreBuilder.noteElements.indexOf(element) + 1
            scoreBuilder.addNote(insertIndex, NoteType.C, 5, duration)
            currentScore = scoreBuilder.build()
            return scoreBuilder.noteElements[insertIndex].id
        }
        return null
    }

}

