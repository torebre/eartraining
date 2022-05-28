package com.kjipo.score

interface HighlightableElement: ScoreElementMarker {

    val id: String

    val properties: Map<String, String>

    fun getIdsOfHighlightElements(): Collection<String>

}
