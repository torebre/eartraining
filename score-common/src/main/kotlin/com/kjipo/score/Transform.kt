package com.kjipo.score

import kotlinx.serialization.Serializable

@Serializable
sealed class Transform {

    data class Translation(val xShift: Int, val yShift: Int): Transform()

}