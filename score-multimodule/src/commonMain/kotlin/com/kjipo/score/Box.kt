package com.kjipo.score

import com.kjipo.svg.PathCommand
import com.kjipo.svg.PathElement
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.findBoundingBox

class Box(
    val xPosition: Int,
    val yPosition: Int,
    val width: Int,
    val height: Int,
    val id: String
) :
    ScoreRenderingElement() {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        return listOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), yPosition.toDouble())),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(height.toDouble())),
            PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(width.toDouble())),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(height.times(-1).toDouble()))
        ).let { pathElements ->
            listOf(
                AbsolutelyPositionedRenderingElement(
                    listOf(PathInterfaceImpl(pathElements, 1)),
                    findBoundingBox(pathElements),
                    id,
                    "debug_box"
                )
            )
        }
    }

    override fun toString(): String {
        return "Box(xPosition=$xPosition, yPosition=$yPosition, width=$width, height=$height, id='$id')"
    }


}