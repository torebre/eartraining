package com.kjipo.svg

import kotlinx.serialization.Serializable

@Serializable
class PathInterfaceImpl(val pathElements: List<PathElement>,
                        val strokeWidth: Int)
