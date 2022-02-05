package com.kjipo.score

import com.kjipo.svg.findBoundingBox


/**
 * Draws a beam element as a rectangle
 */
class BeamElementAbsolutePosition(
    val id: String,
    private val lowerLeftCoordinates: Pair<Double, Double>,
    private val upperRightCoordinates: Pair<Double, Double>
) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val beamElement = addBeam(
            lowerLeftCoordinates.first, lowerLeftCoordinates.second,
            upperRightCoordinates.first, upperRightCoordinates.second
        )

        return listOf(
            AbsolutelyPositionedRenderingElement(
                listOf(beamElement),
                findBoundingBox(beamElement.pathElements),
                id
            )
        )
    }
}