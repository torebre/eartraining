package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import mu.KotlinLogging

class ScoreHandlerSplit(scoreHandlerElements: MutableList<ScoreHandlerElement> = mutableListOf()) :
    ScoreHandlerInterface {
    private val scoreGenerator: ScoreHandlerWithReducedLogic
    private val scoreActionHandler: ScoreActionHandler

    private var dirty = true

    private val logger = KotlinLogging.logger {}

    init {
        val score = ScoreElementsTranslator.createRenderingData(scoreHandlerElements)

        scoreGenerator = ScoreHandlerWithReducedLogic(score)
        scoreActionHandler = ScoreActionHandler(scoreHandlerElements, 0)
    }

    fun clear() {
        scoreActionHandler.clear()
        dirty = true
    }

    override fun getScoreAsJson(): String {
        if (dirty) {
            val scoreHandlerElements = scoreActionHandler.getScoreHandlerElements()
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

    override fun insertNote(keyPressed: Int): String {
        dirty = true
        return scoreActionHandler.insertNote(keyPressed)
    }

    fun insertNote(activeElement: String, duration: Duration): String? {
        dirty = true
        return scoreActionHandler.insertNote(activeElement, duration)
    }

    fun insertNote(duration: Duration): String {
        dirty = true
        return scoreActionHandler.insertNote(duration)
    }

    fun insertNote(duration: Duration, pitch: Int): String {
        dirty = true
        return scoreActionHandler.insertNote(duration, pitch)
    }

    override fun insertNote(activeElement: String, duration: Duration, pitch: Int): String? {
        dirty = true
        return scoreActionHandler.insertNote(activeElement, duration, pitch)
    }

    fun insertChord(duration: Duration, elements: Collection<NoteSequenceElement.NoteElement>) {
        dirty = true
        scoreActionHandler.insertChord(duration, elements)
    }

    fun insertNote(duration: Duration, octave: Int, noteType: NoteType): String {
        dirty = true
        return scoreActionHandler.insertNote(duration, octave, noteType)
    }

    fun insertRest(duration: Duration): String {
        dirty = true
        return scoreActionHandler.insertRest(duration)
    }

    override fun insertRest(activeElement: String, duration: Duration): String {
        dirty = true
        return scoreActionHandler.insertRest(activeElement, duration)
    }

    override fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        dirty = true
        return scoreActionHandler.switchBetweenNoteAndRest(idOfElementToReplace, keyPressed)
    }

    override fun deleteElement(id: String) {
        dirty = true
        scoreActionHandler.deleteElement(id)
    }

    override fun addNoteGroup(duration: Duration, pitches: List<ScoreHandlerInterface.GroupNote>): String? {
        dirty = true
        return scoreActionHandler.addNoteGroup(duration, pitches)
    }

    override fun getHighlightElementsMap() = scoreGenerator.highlightElementMap

    override fun applyOperation(operation: ScoreOperation): String? {
        dirty = true
        return scoreActionHandler.applyOperation(operation)
    }

    fun findNoteType(id: String) = scoreActionHandler.findNoteType(id)

    fun getScoreHandlerElements() = scoreActionHandler.getScoreHandlerElements()

    fun build() = scoreGenerator.build()

}