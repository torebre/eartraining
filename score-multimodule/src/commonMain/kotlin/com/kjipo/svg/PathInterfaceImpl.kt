package com.kjipo.svg

import com.kjipo.score.FILL_COLOUR
import kotlinx.serialization.Serializable

@Serializable
data class PathInterfaceImpl(val pathElements: List<PathElement>,
                        val strokeWidth: Int,
                             val fill:String = FILL_COLOUR)
