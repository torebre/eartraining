package com.kjipo.score

import com.kjipo.handler.ScoreHandlerInterface

class ScoreBuilderSequence(private val scoreData: ScoreSetup) : ScoreHandlerInterface {
    private var noteCounter = 0

    constructor() : this(ScoreSetup())


    override fun getScoreAsJson(): String {
        return scoreData.getScoreAsJson()
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {
        return scoreData.moveNoteOneStep(id, up)
    }

    override fun getIdOfFirstSelectableElement(): String? {
        return scoreData.getIdOfFirstSelectableElement()
    }

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String? {
        return scoreData.getNeighbouringElement(activeElement, lookLeft)
    }

    override fun updateDuration(id: String, keyPressed: Int) {
        return scoreData.updateDuration(id, keyPressed)
    }

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

            // TODO Need to insert note and recalculate the bar data

            scoreData.noteElements.add(insertIndex, NoteElement(NoteType.C, 5, duration, 0, 0, "note-${noteCounter++}"))

//            addNote(insertIndex, NoteType.C, 5, duration)
            return scoreData.noteElements[insertIndex].id
        }
        return null
    }

}