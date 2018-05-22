package com.kjipo.svg


data class BoundingBox(val xMin: Double, val yMin: Double, val xMax: Double, val yMax: Double) {
    fun height(): Double {
        return Math.abs(yMax.minus(yMin))
    }

    fun width(): Double {
        return Math.abs(xMax.minus(xMin))
    }


}

data class CoordinatePair(val x: Double, val y: Double, val skipLine: Boolean = false)


private fun findBoundingBoxInternal(coordinates: Iterable<CoordinatePair>): BoundingBox {
    return BoundingBox(coordinates.map { coordinatePair -> coordinatePair.x }.min() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.y }.min() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.x }.max() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.y }.max() ?: 0.0)
}

fun findBoundingBox(pathElements: List<PathElement>): BoundingBox {
    return findBoundingBoxInternal(processPath(pathElements))
}
