package com.kjipo.score

import com.kjipo.svg.*


class ExtraBarLinesElement(override var xPosition: Int, override var yPosition: Int,
                           val yPositions: List<Int>, val leftStart: Int, val rightEnd: Int) : ScoreRenderingElement {

    companion object {
        var idCounter = 0
    }

    override fun toRenderingElement(): PositionedRenderingElement {
        val pathElements = yPositions.map {
            listOf(
                    PathElement(
                            PathCommand.MOVE_TO_ABSOLUTE, listOf(leftStart.times(-1).toDouble(), yPosition.plus(it).toDouble())),
                    PathElement(
                            PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(leftStart.plus(rightEnd).toDouble())))
        }
                .flatten()
                .toList()

        val yMin = yPositions.min() ?: 0
        val yMax = yPositions.max() ?: yMin

        return PositionedRenderingElement(

                listOf(PathInterfaceImpl(pathElements, 1)),
                BoundingBox(leftStart.times(-1).toDouble(),
                        yMin.toDouble(),
                        leftStart.plus(rightEnd).toDouble(),
                        yMax.toDouble()),
                "bar-${idCounter++}",
                xPosition,
                yPosition)
    }

}


class BarLines(override var xPosition: Int, override var yPosition: Int, val id: String) : ScoreRenderingElement {

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

        val renderingElement = PositionedRenderingElement(
                listOf(PathInterfaceImpl(pathElements, 1)),
                findBoundingBox(pathElements), id,
                0,
                0)

        return renderingElement
    }

}


class Box(override var xPosition: Int, override var yPosition: Int, val width: Int, val height: Int, val id: String) : ScoreRenderingElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val pathElements = listOf(PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), yPosition.toDouble())),
                PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(height.toDouble())),
                PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, listOf(width.toDouble())),
                PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(height.times(-1).toDouble())))

        return PositionedRenderingElement(
                listOf(PathInterfaceImpl(pathElements, 1)),
                findBoundingBox(pathElements),
                id, 0, 0)
    }

}