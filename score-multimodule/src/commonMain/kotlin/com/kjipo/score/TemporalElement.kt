package com.kjipo.score

interface TemporalElement: ScoreElementMarker {
    val id: String
    var duration: Duration

}