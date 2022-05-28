package com.kjipo.score


interface ElementCanBeTied: ScoreElementMarker {

    fun getTieCoordinates(top: Boolean): Pair<Double, Double>

}