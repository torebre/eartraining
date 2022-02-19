package com.kjipo.svg

import kotlinx.serialization.Serializable
import kotlin.math.absoluteValue


@Serializable
data class BoundingBox(val xMin: Double, val yMin: Double, val xMax: Double, val yMax: Double) {
    fun height(): Double {
        return yMax.minus(yMin).absoluteValue
    }

    fun width(): Double {
        return xMax.minus(xMin).absoluteValue
    }


}

data class CoordinatePair(val x: Double, val y: Double, val skipLine: Boolean = false)


private fun findBoundingBoxInternal(coordinates: Iterable<CoordinatePair>): BoundingBox {
    return BoundingBox(
        coordinates.map { coordinatePair -> coordinatePair.x }.minOrNull() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.y }.minOrNull() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.x }.maxOrNull() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.y }.maxOrNull() ?: 0.0)
}

fun findBoundingBox(pathElements: List<PathElement>): BoundingBox {
    return findBoundingBoxInternal(processPath(pathElements))
}
