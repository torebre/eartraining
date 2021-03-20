package com.kjipo.handler

import com.kjipo.score.Duration

interface ReducedScoreInterface {

    fun getScoreAsJson(): String

    fun moveNoteOneStep(id: String, up: Boolean)

    fun getIdOfFirstSelectableElement(): String?

    fun getNeighbouringElement(activeElement: String?, lookLeft: Boolean): String?

    fun updateDuration(id: String, keyPressed: Int)

    fun insertRest(activeElement: String, duration: Duration): String?

    fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String

    fun deleteElement(id: String)

    // Add duration on note level
    fun addNoteGroup(duration: Duration, pitches: List<GroupNote>): String?

//    fun getClientContext(): ClientContext

    fun getHighlightElementsMap(): Map<String, Collection<String>>

    fun applyOperation(operation: ScoreOperation): String?
}