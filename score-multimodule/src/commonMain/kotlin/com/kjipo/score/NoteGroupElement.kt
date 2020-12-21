package com.kjipo.score

import com.kjipo.handler.NoteSymbol
import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*


class NoteGroupElement(
    val notes: List<NoteSymbol>,
    override var duration: Duration,
    override val id: String = "note-${noteElementIdCounter++}"
) : ScoreRenderingElement(), TemporalElement {
    var stem = Stem.NONE
    var partOfBeamGroup = false

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val result = mutableListOf<PositionedRenderingElement>()
        var counter = 0

        var yLowestPosition = Int.MAX_VALUE
        var yHighestPosition = Int.MIN_VALUE

        for (note in notes) {
            yPosition = calculateVerticalOffset(note.noteType, note.octave)

            if(yLowestPosition > yPosition) {
                yLowestPosition = yPosition
            }

            if(yHighestPosition < yPosition) {
                yHighestPosition = yPosition
            }

            addAccidentalIfNeeded(note.id, xPosition, yPosition, note.noteType)?.let {
                result.add(it)
            }


            val glyphData = getNoteHeadGlyph(duration)

            println("xPosition: $xPosition, yPosition: $yPosition")

            val positionedRenderingElement = PositionedRenderingElement.create(
                listOf(PathInterfaceImpl(glyphData.pathElements, 1)), glyphData.boundingBox, "$id-$counter",
                xPosition,
                yPosition
            ).apply {
                // TODO Add correct translation
                yTranslate = -yPosition
            }
            positionedRenderingElement.typeId = duration.name
            result.add(positionedRenderingElement)

            ++counter
        }


        if (requiresStem()) {
            // TODO

//            result.add()
//
//
//            val stemElement = getStem()
//                stemElement.typeId = stem.name
//            result.add(stemElement)

//            if (duration == Duration.EIGHT && !partOfBeamGroup) {
//                // If the note is not part of a beam group, then it should have a flag if the duration requires that it does
//                val stemDirection = stem == Stem.UP
//                val flagGlyph = getFlagGlyph(duration, stemDirection)
//                val positionedRenderingElement = PositionedRenderingElement.create(
//                    listOf(PathInterfaceImpl(flagGlyph.pathElements, 1)), flagGlyph.boundingBox, "$id-$counter",
//                    stemElement.xPosition,
//                    stemElement.yPosition
//                ).apply {
//                    // TODO Need to think about if the stem is going up or down
//                    typeId = flagGlyph.name
//                    yTranslate = -DEFAULT_STEM_HEIGHT
//                    xTranslate = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()
//                }
//
//                result.add(positionedRenderingElement)
//            }
        }


        // TODO Determine if extra bar lines are needed
//        addExtraBarLinesForGClef(
//            note.noteType, note.octave,
//            0,
//            -yPosition,
//            glyphData.boundingBox.xMin.toInt(),
//            glyphData.boundingBox.xMax.toInt()
//        )?.let {
//            result.addAll(it.toRenderingElement())
//        }

        return result
    }

    private fun addAccidentalIfNeeded(
        id: String,
        xPosition: Int,
        yPosition: Int,
        note: NoteType
    ) = if (noteRequiresSharp(note)) {
        setupAccidental(
            id,
            xPosition,
            yPosition,
            Accidental.SHARP
        )
    } else {
        null
    }

    private fun noteRequiresSharp(note: NoteType): Boolean {
        return when (note) {
            NoteType.A_SHARP, NoteType.C_SHARP, NoteType.D_SHARP, NoteType.F_SHARP, NoteType.G_SHARP -> true
            else -> false
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

        if (requiresStem()) {
            // Use the bounding box for the note head of a half note to determine
            // how far to move the stem so that it is on the right side of the note head
            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)

            glyphsUsed[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
        }

        accidentalInUse().forEach { glyphsUsed.put(it.name, getAccidentalGlyph(it)) }

        if (duration == Duration.EIGHT) {
            getFlagGlyph(Duration.EIGHT, stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }

    private fun requiresStem() = notes.map { requiresStem(it) }.filter { it }.any()

    private fun accidentalInUse() = notes.map {
        noteRequiresSharp(it.noteType)
    }.filter { it }.map { Accidental.SHARP }

//    private fun getStem(): PathInterfaceImpl {
//        return if (stem == Stem.UP) {
//            // Use the bounding box for the note head of a half note to determine
//            // how far to move the stem so that it is on the right side of the note head
//            addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
////            definitions[Stem.UP.name] = GlyphData(Stem.UP.name, stem.pathElements, findBoundingBox(stem.pathElements))
//        } else if (stem == Stem.DOWN) {
//            addStem(BoundingBox(0.0, 0.0, 2.0, 0.0), false)
////            definitions[Stem.DOWN.name] = GlyphData(Stem.DOWN.name, stem.pathElements, findBoundingBox(stem.pathElements))
//        }
//    }

    companion object {
        var noteElementIdCounter = 0

        private fun requiresStem(note: NoteSymbol): Boolean {
            // TODO Make proper computation
            return note.duration == Duration.HALF || note.duration == Duration.QUARTER || note.duration == Duration.EIGHT
        }

        private fun setupAccidental(
            id: String,
            xPosition: Int,
            yPosition: Int,
            accidental: Accidental
        ): PositionedRenderingElement {
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
    }

}
