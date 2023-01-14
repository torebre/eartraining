package com.kjipo.score

import com.kjipo.handler.Note
import com.kjipo.handler.NoteSymbol
import com.kjipo.handler.Score
import mu.KotlinLogging

class Context(private val score: Score) {

    private var idCounter = 0
    private var stemCounter = 0
    private var extraBarLinesCounter = 0
    private var tieElementCounter = 0
    private var beamCounter = 0

    val barXspace = 0.0
    val barYspace = 250.0

    var debug = true


    private val logger = KotlinLogging.logger {}


    fun requiresStem(note: NoteSymbol): Boolean {
        // TODO Make proper computation
        return note.duration == Duration.HALF || note.duration == Duration.QUARTER || note.duration == Duration.EIGHT
    }

    fun getAndIncrementIdCounter() = "context-${idCounter++}"

    fun getAndIncrementStemCounter() = "stem-${stemCounter++}"

    fun getAndIncrementExtraBarLinesCounter() = "bar-${extraBarLinesCounter++}"

    fun getAndIncrementTieCounter() = "tie-element-${tieElementCounter++}"
    fun getAndIncrementBeamCounter() = "beam-${beamCounter++}"

    fun shouldNoteFlagBeAdded(id: String): Boolean {
        score.getAllScoreHandlerElements { it.id == id }.firstOrNull()?.let { scoreHandlerElement ->
            if (!(scoreHandlerElement is Note && scoreHandlerElement.duration == Duration.EIGHT)) {
                return false
            }
        }

        val isElementPartOfBeamGroup = score.beamGroups.map { beamGroup ->
            beamGroup.beamLines.mapNotNull { beamLine -> beamLine.elements.find { it.id == id } }.any()
        }.any { it }

        if (isElementPartOfBeamGroup) {
            return false
        }

        return true
    }

}