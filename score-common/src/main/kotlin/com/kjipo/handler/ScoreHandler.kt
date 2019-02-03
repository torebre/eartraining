package com.kjipo.handler

import com.kjipo.score.*
import kotlinx.serialization.json.JSON


class ScoreHandler constructor(val scoreData: ScoreSetup) : ScoreHandlerInterface {
    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var noteCounter = 0

    constructor(init: SCORE.() -> Unit) : this(init.let {
        val scoreBuilder = ScoreBuilderImpl()
        scoreBuilder.score(init)
        scoreBuilder.scoreData
    })

    override fun getScoreAsJson() = JSON.stringify(RenderingSequence.serializer(), scoreData.build())

    override fun moveNoteOneStep(id: String, up: Boolean) = scoreData.moveNoteOneStep(id, up)

    override fun getIdOfFirstSelectableElement() = scoreData.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreData.getNeighbouringElement(activeElement, lookLeft)

    override fun updateDuration(id: String, keyPressed: Int) = scoreData.updateDuration(id, keyPressed)

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        scoreData.noteElements.find { it.id == activeElement }?.let { element ->
            val duration = when (keyPressed) {
                1 -> Duration.QUARTER
                2 -> Duration.HALF
                3 -> Duration.WHOLE
                else -> Duration.QUARTER
            }

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreData.noteElements.indexOf(element) + 1
            addNote(insertIndex, NoteType.C, 5, duration)
            return scoreData.noteElements[insertIndex].id
        }
        return null
    }

    private fun addNote(index: Int, note: NoteType, octave: Int, duration: Duration) {
        val noteElement = NoteElement(note, octave, duration, "note-${noteCounter++}")

        // TODO Need to figure out how bars fit into this
        scoreData.bars.last().scoreRenderingElements.add(index, noteElement)
        scoreData.noteElements.add(index, noteElement)
    }

}

