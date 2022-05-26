package com.kjipo.score

import com.kjipo.svg.*


class ExtraBarLinesElement(
    private val xPosition: Double,
    private val yPosition: Double,
    private val yPositions: List<Int>,
    private val leftStart: Int,
    private val rightEnd: Int,
    private val id: String
) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val xStart = xPosition.minus(leftStart).toDouble()
        val xStop = xStart + rightEnd.toDouble()

        val pathElements = yPositions.map {
            listOf(
                PathElement(
                    PathCommand.MOVE_TO_ABSOLUTE, listOf(xStart, it.toDouble())
                ),
                PathElement(
                    PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(rightEnd.toDouble())
                )
            )
        }
            .flatten()
            .toList()

        val yMin = yPositions.minOrNull() ?: 0
        val yMax = yPositions.maxOrNull() ?: yMin

        return listOf(
            AbsolutelyPositionedRenderingElement(
                listOf(PathInterfaceImpl(pathElements, 1)),
                BoundingBox(
                    xStart,
                    yMin.toDouble(),
                    xStop,
                    yMax.toDouble()
                ),
                id,
                "bar",
            )
        )
    }

}


