package com.kjipo.handler

import com.kjipo.score.Clef
import com.kjipo.score.TimeSignature

class Bar {
    var clef: Clef = Clef.NONE
    var timeSignature: TimeSignature? = null
    var scoreHandlerElements: MutableList<ScoreHandlerElement> = mutableListOf()

    override fun toString(): String {
        return "Bar(clef=$clef, timeSignature=$timeSignature, scoreHandlerElements=$scoreHandlerElements)"
    }

}