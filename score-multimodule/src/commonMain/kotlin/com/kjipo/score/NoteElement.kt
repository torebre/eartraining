package com.kjipo.score

import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*


class NoteElement(
    var note: GClefNoteLine,
    var octave: Int,
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {
    var accidental: Accidental? = null
    var stem = Stem.NONE
    var partOfBeamGroup = false
    val positionedRenderingElements = mutableListOf<PositionedRenderingElement>()

    private val highlightElements = mutableSetOf<String>()


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
            context.getAndIncrementIdCounter(),
            xPosition,
            yPosition
        )
        positionedRenderingElement.typeId = duration.name
        positionedRenderingElements.add(positionedRenderingElement)
        highlightElements.add(positionedRenderingElement.id)

        addExtraBarLinesForGClef(
            note, octave,
            0,
            -yPosition,
            glyphData.boundingBox.xMin.toInt(),
            glyphData.boundingBox.xMax.toInt()
        )?.let {
            positionedRenderingElements.addAll(it.toRenderingElement())
        }
    }


    private fun setupAccidental(accidental: Accidental): PositionedRenderingElement {
        val accidentalGlyph = getAccidentalGlyph(accidental)
        return PositionedRenderingElement.create(
            listOf(PathInterfaceImpl(accidentalGlyph.pathElements, 1)), accidentalGlyph.boundingBox, id,
            xPosition,
            yPosition
        ).apply {
            typeId = accidental.name
            xTranslate = -30
        }
    }

    fun getStem(): PositionedRenderingElement {
        val stem = addStem(getNoteHeadGlyph(duration).boundingBox, stem != Stem.DOWN)

        return PositionedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            "stem-${BarData.barNumber++}-${stemCounter++}"
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
                stemElement.yPosition
            ).apply {
                // TODO Need to think about if the stem is going up or down
                typeId = flagGlyph.name
                yTranslate = -DEFAULT_STEM_HEIGHT
                xTranslate = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()
            }

            positionedRenderingElements.add(positionedRenderingElement)
        }
    }

//    private fun accidentalInUse(): Accidental? {
//        return when {
//            accidental != null -> {
//                accidental
//            }
//            noteRequiresSharp(note) -> {
//                Accidental.SHARP
//            }
//            else -> {
//                null
//            }
//        }
//    }

    override fun getIdsOfHighlightElements() = highlightElements

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, xPosition=$xPosition, yPosition=$yPosition, id='$id')"
    }

}
