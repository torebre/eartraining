package com.kjipo.svg

import kotlinx.serialization.Serializable


@Serializable
class PathElement(val command: PathCommand, val numbers: List<Double>)
