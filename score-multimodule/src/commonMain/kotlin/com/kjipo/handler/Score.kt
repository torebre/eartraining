package com.kjipo.handler


/**
 * Contains information about the elements that should go into the score, but does
 * not say anything about how to render them.
 */
class Score {
    val bars = mutableListOf<Bar>()
    val ties = mutableListOf<Pair<ScoreHandlerElement, ScoreHandlerElement>>()
    val beamGroups = mutableListOf<BeamGroup>()

    private var idCounter = 0

    fun getAndIncrementIdCounter() = "score-${idCounter++}"


    fun getAllScoreHandlerElements(filter: (ScoreHandlerElement) -> Boolean): List<ScoreHandlerElement> {
        return bars.flatMap { it.scoreHandlerElements }.filter(filter)
    }

    override fun toString(): String {
        return "Score(bars=$bars, ties=$ties, idCounter=$idCounter)"
    }

}