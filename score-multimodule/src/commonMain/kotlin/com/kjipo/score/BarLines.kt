package com.kjipo.score

import com.kjipo.svg.PathCommand
import com.kjipo.svg.PathElement
import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.findBoundingBox
import mu.KotlinLogging

class BarLines(
    val xPosition: Double,
    val yPosition: Double,
    val id: String,
    private val properties: ElementWithProperties = Properties()
) : ScoreRenderingElement(), ElementWithProperties by properties {

    private val logger = KotlinLogging.logger {}

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val spaceBetweenLines = 2 * DEFAULT_VERTICAL_NOTE_SPACING
        var y = yPosition - 3 * spaceBetweenLines
        val pathElements = mutableListOf(
            PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), y.toDouble())),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(4.times(spaceBetweenLines).toDouble())),
            PathElement(
                PathCommand.MOVE_TO_ABSOLUTE,
                listOf(xPosition.plus(DEFAULT_BAR_WIDTH), y)
            ),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(4.times(spaceBetweenLines).toDouble()))
        )

        for (i in 0..4) {
            pathElements.add(PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), y.toDouble())))
            pathElements.add(
                PathElement(
                    PathCommand.HORIZONAL_LINE_TO_RELATIVE,
                    listOf(xPosition.plus(DEFAULT_BAR_WIDTH).toDouble())
                )
            )
            y += spaceBetweenLines
        }

        // TODO
        val renderingElement = AbsolutelyPositionedRenderingElement(
            listOf(PathInterfaceImpl(pathElements, 1)),
            findBoundingBox(pathElements), id, "bar",
        )

        return listOf(renderingElement)
    }

}