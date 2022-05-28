package com.kjipo.score

import com.kjipo.handler.Note
import com.kjipo.handler.NoteSymbol
import com.kjipo.handler.ScoreHandlerUtilities.getPitch

class Context {

    private var idCounter = 0
    private var stemCounter = 0
    private var extraBarLinesCounter = 0
    private var tieElementCounter = 0
    private var beamCounter = 0

    val barXspace = 0.0
    val barYspace = 250.0

    var debug = true


    fun requiresStem(note: NoteSymbol): Boolean {
        // TODO Make proper computation
        return note.duration == Duration.HALF || note.duration == Duration.QUARTER || note.duration == Duration.EIGHT
    }

    fun getAndIncrementIdCounter() = "context-${idCounter++}"

    fun getAndIncrementStemCounter() = "stem-${stemCounter++}"

    fun getAndIncrementExtraBarLinesCounter() = "bar-${extraBarLinesCounter++}"

    fun getAndIncrementTieCounter() = "tie-element-${tieElementCounter++}"
    fun getAndIncrementBeamCounter() = "beam-${beamCounter++}"


}