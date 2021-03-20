package com.kjipo.handler

interface ScoreProviderInterface {

    fun getScoreAsJson(): String

    fun getHighlightMap(): Map<String, Collection<String>>

}