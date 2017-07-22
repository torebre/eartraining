package com.kjipo.svg

import com.kjipo.font.GlyphFactory

class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var clef: Clef? = null
    var key: Key = Key.C
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    var totalMeasureWidth = 1000
    var widthAvailableForTemporalElements = totalMeasureWidth

    var nominator = 4
    var denominator = 4

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)

    private fun ticksInMeasure(): Int {
        return nominator * denominatorInTicks()
    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return nominator.div(4).times(TICKS_PER_QUARTER_NOTE)
    }

    fun build(): Pair<List<RenderingElement>, List<Point>> {
        val points = mutableListOf<Point>()
        val clefElement = clef?.let { ClefElement(it, 0, 0)}

        clefElement?.let { println(it.toRenderingElement().boundingBox) }

        widthAvailableForTemporalElements = totalMeasureWidth.minus(clefElement?.
                let { clefElement.toRenderingElement().boundingBox.xMax.minus(clefElement.toRenderingElement().boundingBox.xMin).toInt() } ?: 0)

        // TODO Figure out better solution
        val pixelsPerTick = widthAvailableForTemporalElements / ticksInMeasure()
        val xOffset = totalMeasureWidth - widthAvailableForTemporalElements

        points.addAll(scoreRenderingElements.map { Point(xOffset + it.xPosition * pixelsPerTick, it.yPosition) })
        clefElement?.let { points.add(0, Point(clefElement.xPosition, clefElement.yPosition)) }

        val returnList: List<RenderingElement> = clefElement?.let { scoreRenderingElements.map { it.toRenderingElement() }.toMutableList()
                .let { it.add(0, clefElement.toRenderingElement()); it} } ?: scoreRenderingElements.map { it.toRenderingElement() }

        return Pair(returnList, points.toList())
    }

}