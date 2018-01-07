package com.kjipo.svg

import com.kjipo.font.*

class ExtraBarLinesElement(override var xPosition: Int, override var yPosition: Int,
                           val yPositions: List<Int>, val lineLength: Int) : ScoreRenderingElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val pathElements = yPositions.map {
            listOf(
                    PathElement(
                            PathCommand.MOVE_TO_ABSOLUTE, listOf(-lineLength.div(2.0), yPosition.plus(it).toDouble())),
                    PathElement(
                            PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(lineLength.toDouble())))
        }
                .flatten()
                .toList()

        val yMin = yPositions.min() ?: 0
        val yMax = yPositions.max() ?: yMin

        val renderingElement = RenderingElementImpl(listOf(PathInterfaceImpl(pathElements, 1)),
                BoundingBox(-lineLength.div(2.0),
                        yMin.toDouble(),
                        lineLength.div(2.0),
                        yMax.toDouble()),
                0)

        renderingElement.xPosition = xPosition
        renderingElement.yPosition = yPosition

        return renderingElement
    }


}


class BarLines(override var xPosition: Int, override var yPosition: Int) : ScoreRenderingElement {

//    val width = DEFAULT_BAR_WIDTH
//    val spaceBetweenLines = 2 * DEFAULT_VERTICAL_NOTE_SPACING
//
//    val x = xStart
//    var y = gLine - spaceBetweenLines * 3
//
//    drawLine(x, y, x, y + 4 * spaceBetweenLines, element, 1)
//    drawLine(x + width, y, x + width, y + 4 * spaceBetweenLines, element, 1)
//    for (i in 0..4) {
//        drawLine(x, y, x + width, y, element, 1)
//        y += spaceBetweenLines
//    }


    override fun toRenderingElement(): PositionedRenderingElement {
        val spaceBetweenLines = 2 * DEFAULT_VERTICAL_NOTE_SPACING
        var y = yPosition - 3 * spaceBetweenLines
        val pathElements = mutableListOf(
                PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), y.toDouble())),
                PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(4.times(spaceBetweenLines).toDouble())),
                PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.plus(DEFAULT_BAR_WIDTH).toDouble(), y.toDouble())),
                PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(4.times(spaceBetweenLines).toDouble())))

        for (i in 0..4) {
            pathElements.add(PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), y.toDouble())))
            pathElements.add(PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(xPosition.plus(DEFAULT_BAR_WIDTH).toDouble())))
            y += spaceBetweenLines
        }

        val renderingElement = RenderingElementImpl(listOf(PathInterfaceImpl(pathElements, 1)),
                findBoundingBox(pathElements),
                0)

        renderingElement.xPosition = 0
        renderingElement.yPosition = 0

        return renderingElement
    }


}