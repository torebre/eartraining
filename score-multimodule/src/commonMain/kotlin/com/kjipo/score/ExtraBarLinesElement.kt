package com.kjipo.score

import com.kjipo.svg.*


class ExtraBarLinesElement(
    private val xPosition: Int,
    private val yPosition: Int,
    private val yPositions: List<Int>,
    private val leftStart: Int,
    private val rightEnd: Int,
    private val id: String
) : ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val pathElements = yPositions.map {
            listOf(
                PathElement(
                    PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.minus(leftStart).toDouble(), it.toDouble())
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
                    leftStart.times(-1).toDouble(),
                    yMin.toDouble(),
                    leftStart.plus(rightEnd).toDouble(),
                    yMax.toDouble()
                ),
                id,
                "bar",
            )
        )
    }

}


