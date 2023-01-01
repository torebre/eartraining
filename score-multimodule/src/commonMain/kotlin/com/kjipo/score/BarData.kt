package com.kjipo.score

import com.kjipo.handler.Bar
import com.kjipo.handler.ScoreHelperFunctions.createTemporalElement
import com.kjipo.handler.ScoreHelperFunctions.transformToNoteAndAccidental
import com.kjipo.svg.GlyphData
import mu.KotlinLogging
import kotlin.math.ceil

/**
 * Contains data for rendering a bar and the elements it contains.
 */
class BarData(
    private val context: Context,
    private val bar: Bar,
    internal val barXoffset: Double,
    internal val barYoffset: Double,
    private val debug: Boolean = false
) {
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    private var clef: Clef = Clef.NONE
    private var timeSignature = TimeSignature(0, 0)
    private val definitions = mutableMapOf<String, GlyphData>()

    private val logger = KotlinLogging.logger {}


    fun doLayout() {
        clef = bar.clef
        bar.timeSignature?.run {
            timeSignature = this
        }

        definitions.clear()
        scoreRenderingElements.clear()

        for (element in bar.scoreHandlerElements) {
            scoreRenderingElements.add(createTemporalElement(element, context))
        }

        val clefElement = getClefElement(definitions)
        val timeSignatureElement = getTimeSignatureElement()

        val widthAvailableForTemporalElements = getWidthAvailable(clefElement, timeSignatureElement)

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
                    val xPosition = barXoffset + ceil(xOffset.plus(tickCounter.times(pixelsPerTick)))
                    var yPosition = barYoffset

                    when (scoreRenderingElement) {
                        is NoteElement -> {
                            transformToNoteAndAccidental(scoreRenderingElement.note.noteType).let { noteLineAndAccidental ->
                                // This updating of the y-position is done because when references are used, the y-position is not used, the translate is used instead
                                yPosition += calculateVerticalOffset(
                                    noteLineAndAccidental.first,
                                    scoreRenderingElement.note.octave
                                )
                                scoreRenderingElement.translation = Translation(xPosition, yPosition)
                                scoreRenderingElement.layoutNoteHeads()
                            }
                        }
                        is NoteGroupElement -> {
                            scoreRenderingElement.translation = Translation(xPosition, yPosition)
                            scoreRenderingElement.layoutNoteHeads()
                        }
                        else -> {
                            scoreRenderingElement.translation = Translation(xPosition, yPosition)
                        }
                    }
                    tickCounter += scoreRenderingElement.duration.ticks
                }
            }
        }

        for (scoreRenderingElement in scoreRenderingElements) {
            definitions.putAll(scoreRenderingElement.getGlyphs())
        }

        scoreRenderingElements.add(BarLines(barXoffset, barYoffset, "bar-line"))
    }

    fun getRenderingSequence(): RenderingSequence {
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


    private fun getClefElement(
        definitions: MutableMap<String, GlyphData>
    ): ClefElement? {
        return if (clef == Clef.NONE) {
            null
        } else {
            val element = ClefElement(clef, "clef")
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

    fun getBarXStart(): Double {
        return barXoffset
    }

    fun getBarXEnd(): Double {
        return barXoffset + DEFAULT_BAR_WIDTH
    }

    override fun toString(): String {
        return "BarData(scoreRenderingElements=$scoreRenderingElements)"
    }

}