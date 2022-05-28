package com.kjipo.handler

import com.kjipo.score.ScoreElementMarker


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



    override fun toString(): String {
        return "Score(bars=$bars, ties=$ties, idCounter=$idCounter)"
    }

}