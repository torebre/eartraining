package com.kjipo.score

import com.kjipo.svg.PathCommand
import com.kjipo.svg.PathElement
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.findBoundingBox

class TieElement(
    val id: String,
    private val fromCoordinates: Pair<Double, Double>,
    private val toCoordinates: Pair<Double, Double>
) :
    ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val xDiff = toCoordinates.first - fromCoordinates.first
        val xPoint1 = xDiff.div(3.0)
        val xPoint2 = xDiff.div(3.0).times(2.0)

        val yDiff = toCoordinates.second - fromCoordinates.second
        val yPoint1 = -10.0
        val yPoint2 = -10.0

        val tieElement = PathInterfaceImpl(
            listOf(
                PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(fromCoordinates.first, fromCoordinates.second)),
                PathElement(PathCommand.CURVE_TO_RELATIVE, listOf(xPoint1, yPoint1, xPoint2, yPoint2, xDiff, yDiff))
            ),
            TIE_STROKE_WIDTH,
            "transparent",
            stroke = STROKE_COLOUR
        )

        return listOf(
            AbsolutelyPositionedRenderingElement(
                listOf(tieElement),
                findBoundingBox(tieElement.pathElements), id,
                null
            )
        )
    }

}