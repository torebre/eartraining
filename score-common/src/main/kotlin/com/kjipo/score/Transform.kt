package com.kjipo.score

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
//import kotlinx.serialization.PolymorphicSerializer

@Serializable
sealed class Transform {

    @Serializable
    @SerialName("Transform\$Translation") //(with = PolymorphicSerializer::class)
    data class Translation(
            val xShift: Int, val yShift: Int): Transform()

}