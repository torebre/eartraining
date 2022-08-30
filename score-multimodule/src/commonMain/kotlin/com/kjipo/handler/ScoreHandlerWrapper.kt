package com.kjipo.handler

import com.kjipo.score.Duration
import mu.KotlinLogging

class ScoreHandlerWrapper(var scoreHandler: ScoreHandlerInterface) : ScoreHandlerInterface {
    private val logger = KotlinLogging.logger {}

    private val listeners = mutableListOf<ScoreHandlerListener>()

//     fun updateDuration(id: String, keyPressed: Int) {
//        scoreHandler.updateDuration(id, keyPressed)
//        listeners.forEach { it.pitchSequenceChanged() }
//    }

    override fun getScoreAsJson() = scoreHandler.getScoreAsJson()

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

    override fun insertRest(activeElement: String, duration: Duration): String? =
        scoreHandler.insertRest(activeElement, duration)

    override fun addNoteGroup(duration: Duration, pitches: List<GroupNote>) =
        scoreHandler.addNoteGroup(duration, pitches)

    override fun getHighlightElementsMap() = scoreHandler.getHighlightElementsMap()

    override fun applyOperation(operation: PitchSequenceOperation) {
        scoreHandler.applyOperation(operation)
        listeners.forEach { it.pitchSequenceChanged() }
    }

    fun addListener(scoreHandlerListener: ScoreHandlerListener) = listeners.add(scoreHandlerListener)

    fun removeListener(scoreHandlerListener: ScoreHandlerListener) = listeners.remove(scoreHandlerListener)

}


interface ScoreHandlerListener {

    fun pitchSequenceChanged()

}