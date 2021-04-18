package com.kjipo.score

import com.kjipo.handler.Bar
import com.kjipo.handler.ScoreHelperFunctions.createTemporalElement
import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.findBoundingBox
import com.kjipo.svg.getNoteHeadGlyph
import mu.KotlinLogging
import kotlin.math.ceil

class BarData(private val context: Context, private val bar: Bar, private val debug: Boolean = false) {
    private var clef: Clef = Clef.NONE
    var scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

    var widthAvailableForTemporalElements = DEFAULT_BAR_WIDTH

    private var timeSignature = TimeSignature(0, 0)

    private val logger = KotlinLogging.logger {}

    fun build(barXoffset: Int = 0, barYoffset: Int = 0): RenderingSequence {
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
        val renderGroups = mutableListOf<RenderGroup>()

        clefElement?.let { renderGroups.add(RenderGroup(clefElement.toRenderingElement(), null)) }
        timeSignatureElement?.let { renderGroups.add(RenderGroup(timeSignatureElement.toRenderingElement(), null)) }

        var tickCounter = 0

        // Put notes into x and y positions in the bar
        for (scoreRenderingElement in scoreRenderingElements) {
            when (scoreRenderingElement) {
                is TemporalElement -> {
                    val xPosition = barXoffset + ceil(xOffset.plus(tickCounter.times(pixelsPerTick))).toInt()
                    var yPosition = barYoffset
                    scoreRenderingElement.xPosition = 0

                    if (scoreRenderingElement is NoteElement) {
                        // This updating of the y-position is done because when references are used, the y-position is not used, the translate is used instead
                        yPosition += calculateVerticalOffset(scoreRenderingElement.note, scoreRenderingElement.octave)
                        scoreRenderingElement.layoutNoteHeads()
                    } else if (scoreRenderingElement is NoteGroupElement) {
//                        yPosition += scoreRenderingElement.yPosition
                        scoreRenderingElement.layoutNoteHeads()
                    }
                    tickCounter += scoreRenderingElement.duration.ticks

                    if (debug) {
                        addDebugBox(
                            barXoffset,
                            xOffset,
                            tickCounter,
                            pixelsPerTick,
                            scoreRenderingElement,
                            renderGroups
                        )
                    }

                    scoreRenderingElement.translation = Translation(xPosition, yPosition)
                }
            }
        }

        // Add stems
        for (scoreRenderingElement in scoreRenderingElements) {
            when (scoreRenderingElement) {
                is TemporalElement -> {
                    if (scoreRenderingElement is NoteElement) {
                        if (context.requiresStem(scoreRenderingElement)) {
                            addStem(scoreRenderingElement, definitions)
                        }
                    } else if (scoreRenderingElement is NoteGroupElement) {
                        if (context.requiresStem(scoreRenderingElement)) {
                            addStem(scoreRenderingElement, definitions)
                        }
                    }
                }
            }
        }

        for (scoreRenderingElement in scoreRenderingElements) {
            definitions.putAll(scoreRenderingElement.getGlyphs())

            // TODO Make more clear
            scoreRenderingElement.translation?.let { translation ->
                val renderGroup = RenderGroup(scoreRenderingElement.toRenderingElement(), translation)
                renderGroups.add(renderGroup)
                scoreRenderingElement.renderGroup = renderGroup
            }
        }

        renderGroups.add(RenderGroup(BarLines(barXoffset, barYoffset, "bar-line").toRenderingElement(), null))

        return RenderingSequence(
            renderGroups,
            determineViewBox(renderGroups.flatMap { it.renderingElements }),
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
        scoreRenderingElement: ScoreRenderingElement,
        renderGroups: MutableList<RenderGroup>
    ) {
        val width = barXoffset.plus(ceil(xOffset.plus(tickCounter.times(pixelsPerTick))))
            .minus(scoreRenderingElement.xPosition).toInt()
        val debugBox = Box(
            scoreRenderingElement.xPosition,
            scoreRenderingElement.yPosition,
            width,
            scoreRenderingElement.yPosition,
            "debug"
        )
        renderGroups.add(RenderGroup(debugBox.toRenderingElement(), null))
    }

    private fun addStem(
        scoreRenderingElement: NoteElement,
        definitions: MutableMap<String, GlyphData>
    ) {
        scoreRenderingElement.stem = context.stemUp(scoreRenderingElement.id)

        if (scoreRenderingElement.stem == Stem.UP) {
            scoreRenderingElement.addStem()

            // Use the bounding box for the note head of a half note to determine
            // how far to move the stem so that it is on the right side of the note head
            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
            definitions[Stem.UP.name] = GlyphData(Stem.UP.name, stem.pathElements, findBoundingBox(stem.pathElements))
        } else if (scoreRenderingElement.stem == Stem.DOWN) {
            scoreRenderingElement.addStem()

            val stem = addStem(BoundingBox(0.0, 0.0, 2.0, 0.0), false)
            definitions[Stem.DOWN.name] =
                GlyphData(Stem.DOWN.name, stem.pathElements, findBoundingBox(stem.pathElements))
        }
    }

    private fun addStem(
        scoreRenderingElement: NoteGroupElement,
        definitions: MutableMap<String, GlyphData>
    ) {
        scoreRenderingElement.stem = context.stemUp(scoreRenderingElement.id)

        if (scoreRenderingElement.stem == Stem.UP) {
            scoreRenderingElement.addStem()
            // Use the bounding box for the note head of a half note to determine
            // how far to move the stem so that it is on the right side of the note head
            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
            definitions[Stem.UP.name] = GlyphData(Stem.UP.name, stem.pathElements, findBoundingBox(stem.pathElements))
        } else if (scoreRenderingElement.stem == Stem.DOWN) {
            scoreRenderingElement.addStem()
            val stem = addStem(BoundingBox(0.0, 0.0, 2.0, 0.0), false)
            definitions[Stem.DOWN.name] =
                GlyphData(Stem.DOWN.name, stem.pathElements, findBoundingBox(stem.pathElements))
        }
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