package com.kjipo.svg

import com.kjipo.font.*

class ExtraBarLinesElement(override var xPosition: Int, override var yPosition: Int,
                           val yPositions: List<Int>, val lineLength: Int) : ScoreRenderingElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val pathElements = yPositions.map {

            println("Test23: " +-lineLength.div(2.0) +", " +yPosition.plus(it).toDouble());

            listOf(
                    PathElement(
                            PathCommand.MOVE_TO_ABSOLUTE, listOf(-lineLength.div(2.0), yPosition.plus(it).toDouble())),
                    PathElement(
                            PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(lineLength.toDouble())))
        }
                .flatten()
                .toList()

        println("xPosition: ${xPosition}. yPosition: ${yPosition}")

        val yMin = yPositions.min() ?: 0
        val yMax = yPositions.max() ?: yMin

        val renderingElement = RenderingElementImpl(listOf(PathInterfaceImpl(pathElements, 1)),
                BoundingBox(-lineLength.div(2.0),
                        yMin.toDouble(),
                        lineLength.div(2.0),
                        yMax.toDouble()))

        renderingElement.xPosition = xPosition
        renderingElement.yPosition = yPosition

        return renderingElement
    }


}