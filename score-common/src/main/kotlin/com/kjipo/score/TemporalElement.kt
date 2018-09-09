package com.kjipo.score

interface TemporalElement: ScoreRenderingElement {
    val id: String
    var duration: Duration

}