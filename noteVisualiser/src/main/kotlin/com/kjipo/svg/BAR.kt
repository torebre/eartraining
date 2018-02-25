package com.kjipo.svg

import org.slf4j.LoggerFactory

class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var clef: Clef? = null
    var key: Key = Key.C
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()
    var previousBar: BAR? = null


    var widthAvailableForTemporalElements = DEFAULT_BAR_WIDTH

    var timeSignature = TimeSignature(0, 0)

    private val timeSignatureXOffset = 80
    private val timeSignatureYOffset = -25

    private val logger = LoggerFactory.getLogger(BAR::class.java)

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)

    private fun ticksInMeasure(): Int {
        // TODO Need to look at time signature defined in earlier bars
        return if (timeSignature.denominator == 0) {
            4
        } else {
            timeSignature.nominator * denominatorInTicks()
        }
    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return timeSignature.nominator.div(4).times(TICKS_PER_QUARTER_NOTE)
    }

    fun build(barXoffset: Int = 0, barYoffset: Int = 0): List<PositionedRenderingElement> {
        val clefElement = clef?.let { ClefElement(it, 0, 0) }

        val timeSignatureElement = if (timeSignature.nominator == 0) {
            null
        } else {
            timeSignature.let { TimeSignatureElement(timeSignature.nominator, timeSignature.denominator, timeSignatureXOffset, timeSignatureYOffset) }
        }

        widthAvailableForTemporalElements = DEFAULT_BAR_WIDTH
                .minus(clefElement?.
                        let {
                            val renderingElement = it.toRenderingElement()
                            renderingElement.boundingBox.xMax.minus(renderingElement.boundingBox.xMin).toInt()
                        } ?: 0)
                .minus(timeSignatureElement?.
                        let {
                            val renderingElement = it.toRenderingElement()
                            renderingElement.boundingBox.xMax.minus(renderingElement.boundingBox.xMin).toInt()
                        } ?: 0)

        // TODO Figure out better solution
        val pixelsPerTick = widthAvailableForTemporalElements.toDouble() / ticksInMeasure()
        val xOffset = DEFAULT_BAR_WIDTH - widthAvailableForTemporalElements

        val returnList = mutableListOf<PositionedRenderingElement>()

        clefElement?.let { returnList.add(0, clefElement.toRenderingElement()) }
        timeSignatureElement?.let { returnList.add(0, timeSignatureElement.toRenderingElement()) }

        var tickCounter = 0
        scoreRenderingElements.forEach {
            when (it) {
                is NoteElement -> {
                    it.xPosition = barXoffset + Math.ceil(xOffset.plus(tickCounter.times(pixelsPerTick))).toInt()
                    it.yPosition += barYoffset
                    tickCounter += it.duration.ticks
                }
                else -> logger.error("Unhandled class: ${it::class}")
            }
        }

        returnList.addAll(scoreRenderingElements.map { it.toRenderingElement() })

        scoreRenderingElements.filter { it is NoteElement }
                .map { addExtraBarLines(it as NoteElement) }
                .filterNotNull()
                .let { returnList.addAll(it.map { it.toRenderingElement() }) }

        returnList.add(BarLines(barXoffset, barYoffset).toRenderingElement())

        return returnList
    }

}
