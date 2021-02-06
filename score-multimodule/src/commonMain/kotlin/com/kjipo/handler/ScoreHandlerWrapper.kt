package com.kjipo.handler

import com.github.aakira.napier.Napier
import com.kjipo.score.Accidental
import com.kjipo.score.Duration

class ScoreHandlerWrapper(var scoreHandler: ScoreHandlerInterface) : ScoreHandlerInterface {
    private val listeners = mutableListOf<ScoreHandlerListener>()

    override fun updateDuration(id: String, keyPressed: Int) {
        scoreHandler.updateDuration(id, keyPressed)
        listeners.forEach { it.pitchSequenceChanged() }
    }

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        return scoreHandler.insertNote(activeElement, keyPressed)?.let { idInsertedNote ->
            listeners.forEach { it.pitchSequenceChanged() }
            idInsertedNote
        }
    }


    override fun getScoreAsJson(): String {
        val score = scoreHandler.getScoreAsJson()
        Napier.d("Returning score: $score", tag = "Webscore")
        return score
    }


    override fun moveNoteOneStep(id: String, up: Boolean) {
        scoreHandler.moveNoteOneStep(id, up)
        listeners.forEach { it.pitchSequenceChanged() }
    }


    override fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) =
        scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int) =
        scoreHandler.switchBetweenNoteAndRest(idOfElementToReplace, keyPressed)

    override fun deleteElement(id: String) = scoreHandler.deleteElement(id)

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int): String? =
        scoreHandler.insertNote(activeElement, duration, pitch)

    override fun insertNote(keyPressed: Int) = scoreHandler.insertNote(keyPressed)

    override fun insertRest(activeElement: String, duration: Duration): String? =
        scoreHandler.insertRest(activeElement, duration)

    override fun toggleExtra(id: String, extra: Accidental) = scoreHandler.toggleExtra(id, extra)

    override fun addNoteGroup(duration: Duration, pitches: List<ScoreHandlerInterface.GroupNote>) =
        scoreHandler.addNoteGroup(duration, pitches)

    override fun getHighlightElementsMap() = scoreHandler.getHighlightElementsMap()

}


interface ScoreHandlerListener {

    fun pitchSequenceChanged()

}