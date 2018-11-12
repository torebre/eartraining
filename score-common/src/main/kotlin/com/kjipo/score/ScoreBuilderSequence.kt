package com.kjipo.score

import com.kjipo.handler.ScoreHandlerInterface

class ScoreBuilderSequence : ScoreHandlerInterface {
    private val scoreData = ScoreSetup()
    private var noteCounter = 0


    override fun getScoreAsJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getIdOfFirstSelectableElement(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateDuration(id: String, keyPressed: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

            scoreData.noteElements.add(insertIndex, NoteElement(NoteType.C, 5, duration, 0, 0, 0, "note-${noteCounter++}"))

//            addNote(insertIndex, NoteType.C, 5, duration)
            return scoreData.noteElements[insertIndex].id
        }
        return null
    }


    private fun updateBars() {
        scoreData.bars.clear()
        


        for (noteElement in scoreData.noteElements) {
            // TODO

            noteElement.duration



        }


    }

}