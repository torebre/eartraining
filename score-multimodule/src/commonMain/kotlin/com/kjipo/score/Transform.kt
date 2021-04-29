package com.kjipo.score

import kotlinx.serialization.Serializable


@Serializable
data class Translation(
    val xShift: Int, val yShift: Int
)