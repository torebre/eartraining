package com.kjipo.handler

class Score {

    val bars = mutableListOf<Bar>()
    val ties = mutableListOf<Pair<ScoreHandlerElement, ScoreHandlerElement>>()

    private var idCounter = 0


    fun getAndIncrementIdCounter() = "context-${idCounter++}"

    override fun toString(): String {
        return "Score(bars=$bars, ties=$ties, idCounter=$idCounter)"
    }


}