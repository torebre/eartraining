package com.kjipo.score

import com.kjipo.handler.Bar
import com.kjipo.handler.ScoreHelperFunctions.createTemporalElement
import com.kjipo.svg.GlyphData
import mu.KotlinLogging
import kotlin.math.ceil

/**
 * Contains data for rendering a bar and the elements it contains.
 */
class BarData(
    private val context: Context,
    private val bar: Bar,
    val barXoffset: Int = 0,
    val barYoffset: Int = 0,
    private val debug: Boolean = false
) {
    private var clef: Clef = Clef.NONE
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    var widthAvailableForTemporalElements = DEFAULT_BAR_WIDTH

    private var timeSignature = TimeSignature(0, 0)

    private val logger = KotlinLogging.logger {}

    fun build(): RenderingSequence {
        clef = bar.clef
        bar.timeSignature?.run {
            timeSignature = this
        }
        for (element in bar.scoreHandlerElements) {
            scoreRenderingElements.add(createTemporalElement(element, context))
        }

        val definitions = mutableMapOf<String, GlyphData>()

        val clefElement = getClefElement(barXoffset, barYoffset, definitions)
        val timeSignatureElement = getTimeSignatureElement()

        widthAvailableForTemporalElements = getWidthAvailable(clefElement, timeSignatureElement)

        val valTotalTicksInBar = scoreRenderingElements.filterIsInstance<TemporalElement>().sumOf { it.duration.ticks }
        val pixelsPerTick = widthAvailableForTemporalElements.toDouble() / valTotalTicksInBar
        val xOffset = DEFAULT_BAR_WIDTH - widthAvailableForTemporalElements

        clefElement?.let { scoreRenderingElements.add(clefElement) }
        timeSignatureElement?.let { scoreRenderingElements.add(timeSignatureElement) }

        var tickCounter = 0

        // Put notes into x and y positions in the bar
        for (scoreRenderingElement in scoreRenderingElements) {
            when (scoreRenderingElement) {
                is TemporalElement -> {
                    val xPosition = barXoffset + ceil(xOffset.plus(tickCounter.times(pixelsPerTick))).toInt()
                    var yPosition = barYoffset

                    if (scoreRenderingElement is NoteElement) {
                        // This updating of the y-position is done because when references are used, the y-position is not used, the translate is used instead
                        yPosition += calculateVerticalOffset(scoreRenderingElement.note, scoreRenderingElement.octave)
                        scoreRenderingElement.translation = Translation(xPosition, yPosition)
                        scoreRenderingElement.layoutNoteHeads()
                    } else if (scoreRenderingElement is NoteGroupElement) {
                        scoreRenderingElement.translation = Translation(xPosition, yPosition)
                        scoreRenderingElement.layoutNoteHeads()
                    } else {
                        scoreRenderingElement.translation = Translation(xPosition, yPosition)
                    }
                    tickCounter += scoreRenderingElement.duration.ticks

                    if (debug) {
                        scoreRenderingElements.add(
                            addDebugBox(
                                barXoffset,
                                xOffset,
                                tickCounter,
                                pixelsPerTick,
                                scoreRenderingElement
                            )
                        )
                    }

                }
            }
        }

        // Add stems
        for (scoreRenderingElement in scoreRenderingElements) {
            when (scoreRenderingElement) {
                is TemporalElement -> {
                    if (scoreRenderingElement is NoteElement) {
                        if (context.requiresStem(scoreRenderingElement)) {
                            addStem(scoreRenderingElement)
                        }
                    } else if (scoreRenderingElement is NoteGroupElement) {
                        if (context.requiresStem(scoreRenderingElement)) {
                            addStem(scoreRenderingElement)
                        }
                    }
                }
            }
        }

        for (scoreRenderingElement in scoreRenderingElements) {
            definitions.putAll(scoreRenderingElement.getGlyphs())
        }

        scoreRenderingElements.add(BarLines(barXoffset, barYoffset, "bar-line"))
        val positionedRenderingElements = scoreRenderingElements.flatMap { it.toRenderingElement() }

        return RenderingSequence(
            positionedRenderingElements,
            determineViewBox(positionedRenderingElements),
            definitions
        )
    }

    private fun getTimeSignatureElement() = if (timeSignature.nominator == 0) {
        null
    } else {
        TimeSignatureElement(
            timeSignature.nominator,
            timeSignature.denominator,
            timeSignatureXOffset,
            timeSignatureYOffset,
            "time"
        )
    }

    private fun addDebugBox(
        barXoffset: Int,
        xOffset: Int,
        tickCounter: Int,
        pixelsPerTick: Double,
        scoreRenderingElement: ScoreRenderingElement
    ): Box {
        val width = barXoffset.plus(ceil(xOffset.plus(tickCounter.times(pixelsPerTick))))
            .minus(scoreRenderingElement.xPosition).toInt()
        val debugBox = Box(
            scoreRenderingElement.xPosition,
            scoreRenderingElement.yPosition,
            width,
            scoreRenderingElement.yPosition,
            "debug"
        )
        return debugBox
    }

    private fun addStem(scoreRenderingElement: NoteElement) {
        scoreRenderingElement.addStem(context.stemUp(scoreRenderingElement.id))
    }

    private fun addStem(scoreRenderingElement: NoteGroupElement) {
        scoreRenderingElement.addStem(context.stemUp(scoreRenderingElement.id))
    }

    private fun getClefElement(
        barXoffset: Int,
        barYoffset: Int,
        definitions: MutableMap<String, GlyphData>
    ): ClefElement? {
        return if (clef == Clef.NONE) {
            null
        } else {
            val element = ClefElement(clef, barXoffset, barYoffset, "clef")
            definitions[clef.name] = element.getGlyphData()
            element
        }
    }

    private fun getWidthAvailable(clefElement: ClefElement?, timeSignatureElement: TimeSignatureElement?): Int {
        return DEFAULT_BAR_WIDTH
            .minus(clefElement?.let {
                val renderingElement = it.toRenderingElement()
                renderingElement[0].boundingBox.xMax.minus(renderingElement[0].boundingBox.xMin).toInt()
            } ?: 0)
            .minus(timeSignatureElement?.let {
                val renderingElement = it.toRenderingElement()
                renderingElement[0].boundingBox.xMax.minus(renderingElement[0].boundingBox.xMin).toInt()
            } ?: 0)
            .minus(START_NOTE_ELEMENT_MARGIN)
    }

    /**
     * Should be called after build
     */
    fun getHighlightElements(): List<ScoreRenderingElement> {
        return scoreRenderingElements.filter { it is HighlightableElement }
    }

    override fun toString(): String {
        return "BarData(scoreRenderingElements=$scoreRenderingElements)"
    }

    companion object {
        var barNumber = 0
        var stemCounter = 0
    }

}