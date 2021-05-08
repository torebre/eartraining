package com.kjipo.score

import com.kjipo.svg.PathCommand
import com.kjipo.svg.PathElement
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.findBoundingBox

class TieElement(val id: String, var xStop: Double, var yStop: Double) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val xDiff = xStop - xPosition
        val xPoint1 = xDiff.div(3.0)
        val xPoint2 = xDiff.div(3.0).times(2.0)

        val yDiff = yStop - yPosition
        val yPoint1 = -10.0
        val yPoint2 = -10.0

        val tieElement = PathInterfaceImpl(
            listOf(
                PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), yPosition.toDouble())),
                PathElement(PathCommand.CURVE_TO_RELATIVE, listOf(xPoint1, yPoint1, xPoint2, yPoint2, xDiff, yDiff))
            ),
            2
        )

        // TODO Should this be a translated or absolutely positioned element?
        return listOf(
            AbsolutelyPositionedRenderingElement(
                listOf(tieElement),
                findBoundingBox(tieElement.pathElements), id
            )
        )
    }

}