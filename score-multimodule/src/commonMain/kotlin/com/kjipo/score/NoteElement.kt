package com.kjipo.score

import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*
import mu.KotlinLogging


class NoteElement(
    var note: GClefNoteLine,
    var octave: Int,
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter(),
    override val properties: Map<String, String> = mapOf()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {
    var accidental: Accidental? = null
    var stem = Stem.NONE
    var partOfBeamGroup = false
    val positionedRenderingElements = mutableListOf<PositionedRenderingElement>()

    private val highlightElements = mutableSetOf<String>()

    private val logger = KotlinLogging.logger {}


    override fun toRenderingElement(): List<PositionedRenderingElement> {
        return positionedRenderingElements
    }

    fun layoutNoteHeads() {
        yPosition = calculateVerticalOffset(note, octave)

        accidental?.run {
            positionedRenderingElements.add(setupAccidental(this))
        }

        val glyphData = getNoteHeadGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement.create(
            listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
            glyphData.boundingBox,
            id,
            xPosition,
            yPosition,
            translation
        )
        positionedRenderingElement.typeId = duration.name
        positionedRenderingElements.add(positionedRenderingElement)
        highlightElements.add(positionedRenderingElement.id)

        addExtraBarLinesForGClef(
            note, octave,
            xPosition,
            0,
            glyphData.boundingBox.xMin.toInt(),
            glyphData.boundingBox.xMax.toInt()
        )?.let { extraBarLinesElement ->
            extraBarLinesElement.translation = Translation(translation?.xShift ?: 0, 0)
            positionedRenderingElements.addAll(extraBarLinesElement.toRenderingElement())
        }
    }


    private fun setupAccidental(accidental: Accidental): PositionedRenderingElement {
        val accidentalGlyph = getAccidentalGlyph(accidental)
        return PositionedRenderingElement.create(
            listOf(PathInterfaceImpl(accidentalGlyph.pathElements, 1)), accidentalGlyph.boundingBox, id,
            xPosition,
            yPosition,
            translation
        ).apply {
            typeId = accidental.name

            if (translation == null) {
                translation = Translation(-30, 0)
            } else {
                translation = translation?.let {
                    Translation(it.xShift - 30, it.yShift)
                }
            }
        }
    }

    fun getStem(): PositionedRenderingElement {
        val stem = addStem(getNoteHeadGlyph(duration).boundingBox, stem != Stem.DOWN)

        return PositionedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            "stem-${BarData.barNumber++}-${stemCounter++}", translation = translation
        )
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

//        if (stem != Stem.NONE) {
//             Use the bounding box for the note head of a half note to determine
//             how far to move the stem so that it is on the right side of the note head
//            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
//            glyphsUsed[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
//        }

        accidental?.let {
            glyphsUsed.put(it.name, getAccidentalGlyph(it))
        }

        if (duration == Duration.EIGHT) {
            getFlagGlyph(Duration.EIGHT, stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }

    fun addStem() {
        val stemElement = getStem()
        stemElement.typeId = stem.name
        positionedRenderingElements.add(stemElement)

        if (duration == Duration.EIGHT && !partOfBeamGroup) {
            // If the note is not part of a beam group, then it should have a flag if the duration requires that it does
            val stemDirection = stem == Stem.UP
            val flagGlyph = getFlagGlyph(duration, stemDirection)
            val positionedRenderingElement = PositionedRenderingElement.create(
                listOf(PathInterfaceImpl(flagGlyph.pathElements, 1)), flagGlyph.boundingBox, id,
                stemElement.xPosition,
                stemElement.yPosition,
                translation
            ).apply {
                // TODO Need to think about if the stem is going up or down
                typeId = flagGlyph.name
                val yTranslate = -DEFAULT_STEM_HEIGHT
                val xTranslate = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()
                if (translation == null) {
                    translation = Translation(xTranslate,yTranslate)
                } else {
                    translation = translation?.let {
                        Translation(it.xShift + xTranslate, it.yShift - yTranslate)
                    }
                }
            }

            positionedRenderingElements.add(positionedRenderingElement)
        }
    }

    override fun getIdsOfHighlightElements() = highlightElements

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, xPosition=$xPosition, yPosition=$yPosition, id='$id')"
    }

}
