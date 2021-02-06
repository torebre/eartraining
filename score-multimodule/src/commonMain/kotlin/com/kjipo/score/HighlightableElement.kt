package com.kjipo.score

interface HighlightableElement {

    val id: String

    fun getIdsOfHighlightElements(): Collection<String>

}
