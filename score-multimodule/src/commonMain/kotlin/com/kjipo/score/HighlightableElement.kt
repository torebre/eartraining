package com.kjipo.score

interface HighlightableElement {

    val id: String

    val properties: Map<String, String>

    fun getIdsOfHighlightElements(): Collection<String>

}
