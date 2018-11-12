package com.kjipo.svg

import kotlinx.serialization.Serializable

@Serializable
data class PathInterfaceImpl(val pathElements: List<PathElement>,
                        val strokeWidth: Int)
