package com.kjipo.score

import com.kjipo.handler.NoteSymbol
import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*
import kotlin.math.absoluteValue


class NoteGroupElement(
    val notes: List<NoteSymbol>,
    override var duration: Duration,
    override val id: String,
    val context: Context
) : ScoreRenderingElement(), TemporalElement {
    val result = mutableListOf<PositionedRenderingElement>()
    var yLowestPosition = Int.MAX_VALUE
    var yHighestPosition = Int.MIN_VALUE
    var stem = Stem.NONE

    constructor(
        notes: List<NoteSymbol>,
        duration: Duration,
        context: Context
    ) : this(
        notes, duration,
        "note-${noteElementIdCounter++}", context
    )



    override fun toRenderingElement(): List<PositionedRenderingElement> {
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

    fun layoutNoteHeads() {
        var counter = 0

        for (note in notes) {
            yPosition = calculateVerticalOffset(note.noteType, note.octave)

            if (yLowestPosition > yPosition) {
                yLowestPosition = yPosition
            }

            if (yHighestPosition < yPosition) {
                yHighestPosition = yPosition
            }

            note.accidental?.let {
                result.add(
                    setupAccidental(
                        id,
                        xPosition,
                        yPosition,
                        it
                    )
                )
            }

            val glyphData = getNoteHeadGlyph(duration)

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


    }


//    private fun addAccidentalIfNeeded(
//        id: String,
//        xPosition: Int,
//        yPosition: Int,
//        note: NoteType
//    ) = if (noteRequiresSharp(note)) {
//        setupAccidental(
//            id,
//            xPosition,
//            yPosition,
//            Accidental.SHARP
//        )
//    } else {
//        null
//    }

//    private fun noteRequiresSharp(note: NoteType): Boolean {
//        return when (note) {
//            NoteType.A_SHARP, NoteType.C_SHARP, NoteType.D_SHARP, NoteType.F_SHARP, NoteType.G_SHARP -> true
//            else -> false
//        }
//    }

    fun getStem(xCoordinate: Int, yCoordinate: Int, stemHeight: Int): PositionedRenderingElement {
        val stem = addStem(xCoordinate, yCoordinate, DEFAULT_STEM_WIDTH, stemHeight, stem != Stem.DOWN)

        return PositionedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            "stem-${BarData.barNumber++}-${stemCounter++}"
        )
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

//        if (requiresStem()) {
//            // Use the bounding box for the note head of a half note to determine
//            // how far to move the stem so that it is on the right side of the note head
//            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
//
//            glyphsUsed[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
//        }

        accidentalInUse().forEach { glyphsUsed.put(it.name, getAccidentalGlyph(it)) }

        if (duration == Duration.EIGHT) {
            getFlagGlyph(Duration.EIGHT, stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }


    private fun accidentalInUse() = notes.mapNotNull {
        it.accidental
    }.distinct()


    fun addStem() {

        // TODO Need to add more functionality to stems: Whether they are pointing up or down and more

        val xCoordinate = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()
        // Not setting stemElement.typeId to avoid references being used, the stem is created specifically for this note group

        val ySpanForNoteGroup = yHighestPosition.minus(yLowestPosition).absoluteValue
        val stemElement = getStem(xCoordinate, yHighestPosition, ySpanForNoteGroup + DEFAULT_STEM_HEIGHT)
//                stemElement.typeId = stem.name +"_" +noteElementIdCounter++
        result.add(stemElement)

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


    companion object {
        var noteElementIdCounter = 0


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
