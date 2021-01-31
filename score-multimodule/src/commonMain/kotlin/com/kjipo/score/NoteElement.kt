package com.kjipo.score

import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*


class NoteElement(
    var note: NoteType,
    var octave: Int,
    override var duration: Duration,
    override val id: String = "note-${noteElementIdCounter++}"
) : ScoreRenderingElement(), TemporalElement {
    var accidental: Accidental? = null
    var stem = Stem.NONE
    var partOfBeamGroup = false
    val positionedRenderingElements = mutableListOf<PositionedRenderingElement>()


    override fun toRenderingElement(): List<PositionedRenderingElement> {
        return positionedRenderingElements
    }

    fun layoutNoteHeads() {
        yPosition = calculateVerticalOffset(note, octave)

        addAccidentalIfNeeded(note)?.let {
            positionedRenderingElements.add(it)
        }

        val glyphData = getNoteHeadGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement.create(
            listOf(PathInterfaceImpl(glyphData.pathElements, 1)), glyphData.boundingBox, id,
            xPosition,
            yPosition
        )
        positionedRenderingElement.typeId = duration.name
        positionedRenderingElements.add(positionedRenderingElement)

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

    private fun addAccidentalIfNeeded(note: NoteType): PositionedRenderingElement? {
        if (noteRequiresSharp(note)) {
            return setupAccidental(Accidental.SHARP)
        }
        return null
    }

    private fun noteRequiresSharp(note: NoteType): Boolean {
        return when (note) {
            NoteType.A_SHARP, NoteType.C_SHARP, NoteType.D_SHARP, NoteType.F_SHARP, NoteType.G_SHARP -> true
            else -> false
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

    fun getClef(): Clef {
        // TODO The clef can change withing a bar. This is not handled at present
        // Defaulting to G
        return Clef.G
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

//        if (stem != Stem.NONE) {
//             Use the bounding box for the note head of a half note to determine
//             how far to move the stem so that it is on the right side of the note head
//            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
//            glyphsUsed[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
//        }

        accidentalInUse()?.let {
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

    private fun accidentalInUse(): Accidental? {
        return when {
            accidental != null -> {
                accidental
            }
            noteRequiresSharp(note) -> {
                Accidental.SHARP
            }
            else -> {
                null
            }
        }
    }

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, xPosition=$xPosition, yPosition=$yPosition, id='$id')"
    }


    companion object {
        var noteElementIdCounter = 0
    }

}
