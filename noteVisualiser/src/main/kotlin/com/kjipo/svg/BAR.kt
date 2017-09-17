package com.kjipo.svg

import com.kjipo.font.findBoundingBox

class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var clef: Clef? = null
    var key: Key = Key.C
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()
    var previousBar: BAR? = null

    var totalMeasureWidth = 1000
    var widthAvailableForTemporalElements = totalMeasureWidth

    var nominator = 4
    var denominator = 4
    var tieGroup: Int = 0

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)

    private fun ticksInMeasure(): Int {
        return nominator * denominatorInTicks()
    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return nominator.div(4).times(TICKS_PER_QUARTER_NOTE)
    }

    fun build(): List<PositionedRenderingElement> {
        val clefElement = clef?.let { ClefElement(it, 0, 0) }

//        clefElement?.let { println(it.toRenderingElement().boundingBox) }

        widthAvailableForTemporalElements = totalMeasureWidth.minus(clefElement?.
                let { clefElement.toRenderingElement().boundingBox.xMax.minus(clefElement.toRenderingElement().boundingBox.xMin).toInt() } ?: 0)

        // TODO Figure out better solution
        val pixelsPerTick = widthAvailableForTemporalElements / ticksInMeasure()
        val xOffset = totalMeasureWidth - widthAvailableForTemporalElements

        val returnList = mutableListOf<PositionedRenderingElement>()

        clefElement?.let { returnList.add(0, clefElement.toRenderingElement()) }


        scoreRenderingElements.forEach { it.xPosition = xOffset + it.xPosition * pixelsPerTick }

        returnList.addAll(scoreRenderingElements.map { it.toRenderingElement() })

        scoreRenderingElements.filter { it is NoteElement }
                .map { addExtraBarLines(it as NoteElement) }
                .filterNotNull()
                .let { returnList.addAll(it.map { it.toRenderingElement() }) }

        scoreRenderingElements.filter { it is NoteElement }
                .map { createStemElement(it as NoteElement) }
                .let { returnList.addAll(it) }

        return returnList
    }

}

private fun createStemElement(noteElement: NoteElement): StemElement {
    // TODO Need to determine stem direction, and whether the note should have a stem
    val stem = addStem(noteElement.toRenderingElement().boundingBox)
    val stemElement = StemElement(noteElement.xPosition, noteElement.yPosition, listOf(stem), findBoundingBox(stem.pathElements))
    noteElement.stemElement = stemElement
    return stemElement
}