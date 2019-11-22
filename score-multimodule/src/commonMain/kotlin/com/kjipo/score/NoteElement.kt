package com.kjipo.score

import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*


class NoteElement(var note: NoteType,
                  var octave: Int,
                  override var duration: Duration,
                  override val id: String = "note-${noteElementIdCounter++}") : ScoreRenderingElement(), TemporalElement {
    var accidental: Accidental? = null
    var stem = Stem.NONE
    var partOfBeamGroup = false

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val result = mutableListOf<PositionedRenderingElement>()
        yPosition = calculateVerticalOffset(note, octave)

        addAccidentalIfNeeded(note)?.let {
            result.add(it)
        }

        if (requiresStem()) {
            val stemElement = getStem()
            stemElement.typeId = stem.name
            result.add(stemElement)

            if (duration == Duration.EIGHT && !partOfBeamGroup) {
                // If the note is not part of a beam group, then it should have a flag if the duration requires that it does
                val stemDirection = stem == Stem.UP
                val flagGlyph = getFlagGlyph(duration, stemDirection)
                val positionedRenderingElement = PositionedRenderingElement.create(listOf(PathInterfaceImpl(flagGlyph.pathElements, 1)), flagGlyph.boundingBox, id,
                        stemElement.xPosition,
                        stemElement.yPosition).apply {
                    // TODO Need to think about is the stem is going up or down
                    typeId = flagGlyph.name
                    yTranslate = -DEFAULT_STEM_HEIGHT
                    xTranslate = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()
                }

                result.add(positionedRenderingElement)
            }
        }

        val glyphData = getNoteHeadGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement.create(listOf(PathInterfaceImpl(glyphData.pathElements, 1)), glyphData.boundingBox, id,
                xPosition,
                yPosition)
        positionedRenderingElement.typeId = duration.name
        result.add(positionedRenderingElement)

        addExtraBarLinesForGClef(note, octave,
                0,
                -yPosition,
                glyphData.boundingBox.xMin.toInt(),
                glyphData.boundingBox.xMax.toInt())?.let {
            result.addAll(it.toRenderingElement())
        }

        return result
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
        return PositionedRenderingElement.create(listOf(PathInterfaceImpl(accidentalGlyph.pathElements, 1)), accidentalGlyph.boundingBox, id,
                xPosition,
                yPosition).apply {
            typeId = accidental.name
            xTranslate = -30
        }
    }

    fun getStem(): PositionedRenderingElement {
        val stem = addStem(getNoteHeadGlyph(duration).boundingBox, stem != Stem.DOWN)

        return PositionedRenderingElement(listOf(stem),
                findBoundingBox(stem.pathElements),
                "stem-${BarData.barNumber++}-${stemCounter++}")
    }

    fun requiresStem(): Boolean {
        // TODO Make proper computation
        return duration == Duration.HALF || duration == Duration.QUARTER || duration == Duration.EIGHT
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


class TieElement(val id: String, var xStop: Double, var yStop: Double) : ScoreRenderingElement() {


    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val xDiff = xStop - xPosition
        val xPoint1 = xDiff.div(3.0)
        val xPoint2 = xDiff.div(3.0).times(2.0)

        val yDiff = yStop - yPosition
        val yPoint1 = -10.0
        val yPoint2 = -10.0

        val tieElement = PathInterfaceImpl(
                listOf(
                        PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), yPosition.toDouble())),
                        PathElement(PathCommand.CURVE_TO_RELATIVE, listOf(xPoint1, yPoint1, xPoint2, yPoint2, xDiff, yDiff))
                ),
                2, fill = "transparent")

        return listOf(PositionedRenderingElement(listOf(tieElement),
                findBoundingBox(tieElement.pathElements), id))
    }

}


class BeamGroup(val noteIds: List<String>)

class TiePair(val startNote: NoteElement, val endNote: NoteElement)


class BeamElement(val id: String, private val start: Pair<Double, Double>, private val stop: Pair<Double, Double>, renderGroup: RenderGroup?) : ScoreRenderingElement(0, 0, renderGroup) {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val beamElement = addBeam(start.first, start.second,
                stop.first, stop.second)

        return listOf(PositionedRenderingElement(listOf(beamElement),
                findBoundingBox(beamElement.pathElements),
                id))
    }
}