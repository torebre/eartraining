package com.kjipo.score


interface ElementCanBeTied : ScoreElementMarker {

    fun getTieCoordinates(top: Boolean): Pair<Double, Double>

}


interface ElementCanBeInBeamGroup : ScoreElementMarker {

    val id: String

    var translation: Translation?


    fun getAbsoluteCoordinatesForEndpointOfStem(): Pair<Double, Double>?

    fun getStem(): TranslatedRenderingElement?

    fun updateStemHeight(stemHeight: Double)

}