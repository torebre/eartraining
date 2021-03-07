package com.kjipo.handler

import com.kjipo.score.Duration
import mu.KotlinLogging

class ScoreHandlerSplit(val scoreHandlerElements: MutableList<ScoreHandlerElement>) : ScoreHandlerInterface {
    private val scoreGenerator: ScoreHandlerWithReducedLogic
    private val scoreActionHandler: ScoreActionHandler

    private var dirty = true

    private val logger = KotlinLogging.logger {}

    init {
        val score = ScoreElementsTranslator.createRenderingData(scoreHandlerElements)

        scoreGenerator = ScoreHandlerWithReducedLogic(score)
        scoreActionHandler = ScoreActionHandler(scoreHandlerElements, 0)
    }


    override fun getScoreAsJson(): String {
        if (dirty) {
            val score = ScoreElementsTranslator.createRenderingData(scoreHandlerElements)
            scoreGenerator.score = score
            dirty = false

            logger.debug { "Number of elements: ${scoreHandlerElements.size}" }
        }

        return scoreGenerator.getScoreAsJson()
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {
        scoreActionHandler.moveNoteOneStep(id, up)
        dirty = true
    }

    override fun getIdOfFirstSelectableElement() = scoreActionHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) =
        scoreActionHandler.getNeighbouringElement(activeElement, lookLeft)

    override fun updateDuration(id: String, keyPressed: Int) {
        scoreActionHandler.updateDuration(id, keyPressed)
        dirty = true
    }

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        dirty = true
        return scoreActionHandler.insertNote(activeElement, keyPressed)
    }

    override fun insertNote(keyPressed: Int): String? {
        dirty = true
        return insertNote(keyPressed)
    }

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int): String? {
        dirty = true
        return insertNote(activeElement, duration, pitch)
    }

    override fun insertRest(activeElement: String, duration: Duration): String? {
        dirty = true
        return insertRest(activeElement, duration)
    }

    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        dirty = true
        return switchBetweenNoteAndRest(idOfElementToReplace, keyPressed)
    }

    override fun deleteElement(id: String) {
        dirty = true
        deleteElement(id)
    }

    override fun addNoteGroup(duration: Duration, pitches: List<ScoreHandlerInterface.GroupNote>): String? {
        dirty = true
        return addNoteGroup(duration, pitches)
    }

    override fun getHighlightElementsMap() = scoreGenerator.highlightElementMap

    override fun applyOperation(operation: ScoreOperation): String? {
        dirty = true
        return scoreActionHandler.applyOperation(operation)
    }
}