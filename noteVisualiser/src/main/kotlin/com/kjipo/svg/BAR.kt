package com.kjipo.svg

class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var clef: Clef? = null
    var key: Key = Key.C
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    var totalMeasureWidth = 1000
    var widthAvailableForTemporalElements = totalMeasureWidth

    var nominator = 4
    var denominator = 4

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)


    private fun layout(): List<Point> {
        // TODO Figure out better solution


        val pixelsPerTick = totalMeasureWidth / ticksInMeasure()

        return scoreRenderingElements.map { Point(it.xPosition * pixelsPerTick, it.yPosition) }
    }

    private fun ticksInMeasure(): Int {
        return nominator * denominatorInTicks()
    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return nominator.div(4).times(TICKS_PER_QUARTER_NOTE)
    }

    fun build(): Pair<List<RenderingElement>, List<Point>> {
        return Pair(scoreRenderingElements.map { it.toRenderingElement() }, layout())
    }

}