package com.kjipo.score

interface HighlightableElement: ScoreElementMarker, ElementWithProperties {

    val id: String

    fun getIdsOfHighlightElements(): Collection<String>

}
