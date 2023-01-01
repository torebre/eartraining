package com.kjipo.score

import com.kjipo.svg.findBoundingBox


/**
 * Draws a beam element as a rectangle
 */
class BeamElementAbsolutePosition(
    val id: String,
    private val upperLeftCoordinates: Pair<Double, Double>,
    private val upperRightCoordinates: Pair<Double, Double>
) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val beamElement = addBeam(
            upperLeftCoordinates.first, upperLeftCoordinates.second - DEFAULT_BEAM_HEIGHT,
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