package com.kjipo.svg

class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var clef: Clef? = null
    var key: Key = Key.C
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()
    var previousBar: BAR? = null

    var totalMeasureWidth = 1000
    var widthAvailableForTemporalElements = totalMeasureWidth

    var timeSignature = TimeSignature(4, 4)

    private val timeSignatureXOffset = 80
    private val timeSignatureYOffset = -25

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)

    private fun ticksInMeasure(): Int {
        return timeSignature.nominator * denominatorInTicks()
    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return timeSignature.nominator.div(4).times(TICKS_PER_QUARTER_NOTE)
    }

    fun build(): List<PositionedRenderingElement> {
        val clefElement = clef?.let { ClefElement(it, 0, 0) }



        val timeSignatureElement = if(timeSignature.nominator == 0) {
            null
        }
        else {
            timeSignature.let { TimeSignatureElement(timeSignature.nominator, timeSignature.denominator, timeSignatureXOffset, timeSignatureYOffset) }
        }


        widthAvailableForTemporalElements = totalMeasureWidth.minus(clefElement?.
                let { clefElement.toRenderingElement().boundingBox.xMax.minus(clefElement.toRenderingElement().boundingBox.xMin).toInt() } ?: 0)

        // TODO Figure out better solution
        val pixelsPerTick = widthAvailableForTemporalElements / ticksInMeasure()
        val xOffset = totalMeasureWidth - widthAvailableForTemporalElements

        val returnList = mutableListOf<PositionedRenderingElement>()

        clefElement?.let { returnList.add(0, clefElement.toRenderingElement()) }

        timeSignatureElement?.let { returnList.add(0, timeSignatureElement.toRenderingElement()) }


        scoreRenderingElements.forEach { it.xPosition = xOffset + it.xPosition * pixelsPerTick }

        returnList.addAll(scoreRenderingElements.map { it.toRenderingElement() })

        scoreRenderingElements.filter { it is NoteElement }
                .map { addExtraBarLines(it as NoteElement) }
                .filterNotNull()
                .let { returnList.addAll(it.map { it.toRenderingElement() }) }

        return returnList
    }

}
