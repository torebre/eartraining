package com.kjipo.handler


interface ScoreHandlerInterface {

    fun getScoreAsJson(): String

    fun moveNoteOneStep(id: String, up: Boolean)

    fun getIdOfFirstSelectableElement(): String?

    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String?

    fun updateDuration(id: String, keyPressed: Int)

    fun insertNote(activeElement: String, keyPressed: Int): String?

    fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String

    fun deleteElement(id: String)

}