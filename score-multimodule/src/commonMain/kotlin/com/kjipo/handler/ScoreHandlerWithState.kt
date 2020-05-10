package com.kjipo.handler

interface ScoreHandlerWithState {

    fun getScoreAsJson(): String

    fun getIdOfFirstSelectableElement(): String?

    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String?

    fun applyOperation(operation: ScoreOperation): String?

}