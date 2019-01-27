package com.kjipo.score

import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*


class NoteElement(var note: NoteType,
                  var octave: Int,
                  override var duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  override val id: String) : ScoreRenderingElement, TemporalElement {
    var tie: String? = null
    var accidental: Accidental? = null

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val result = mutableListOf<PositionedRenderingElement>()
        yPosition = calculateVerticalOffset(note, octave)

        accidental?.let {
            val accidentalGlyph = getGlyph(it)
            val positionedRenderingElement = PositionedRenderingElement.create(listOf(PathInterfaceImpl(accidentalGlyph.pathElements, 1)), accidentalGlyph.boundingBox, id,
                    xPosition,
                    yPosition)
            positionedRenderingElement.typeId = it.name
            positionedRenderingElement.xTranslate = -30

            result.add(positionedRenderingElement)
        }

        val glyphData = getGlyph(duration)
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


        if (requiresStem()) {
            // TODO Determine whether the stem should go up or down
            val stem = addStem(glyphData.boundingBox)

            val stemElement = PositionedRenderingElement(listOf(stem),
                    findBoundingBox(stem.pathElements),
                    "stem-${BarData.barNumber++}-${stemCounter++}",
                    0,
                    0)
            stemElement.typeId = STEM_UP

//            definitions[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))

            result.add(stemElement)
        }

        return result
    }

    fun requiresStem(): Boolean {
        // TODO Make proper computation
        return duration == Duration.HALF || duration == Duration.QUARTER
    }

    fun getClef(): Clef {
        // TODO The clef can change withing a bar. This is not handled at present
        // Defaulting to G
        return Clef.G
    }


    fun getGlyphsUsed(): Collection<String> {
        val glyphsUsed = mutableListOf<String>()


        if (requiresStem()) {
            glyphsUsed.add(STEM_UP)
        }

        accidental?.let {
            glyphsUsed.add(it.name)
        }

        glyphsUsed.add(duration.name)

        return glyphsUsed
    }

    fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

        if (requiresStem()) {
            // Use the bounding box for the note head of a half note to determine
            // how far to move the stem so that it is on the right side of the note head
            val stem = addStem(getGlyph(Duration.HALF).boundingBox)

            glyphsUsed.put(STEM_UP, GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements)))
        }

        accidental?.let {
            glyphsUsed.put(it.name, getGlyph(it))
        }

        glyphsUsed.put(duration.name, getGlyph(Duration.QUARTER))

        return glyphsUsed
    }

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, xPosition=$xPosition, yPosition=$yPosition, id='$id')"
    }


}


class TieElement(val id: String, override var xPosition: Int,
                 override var yPosition: Int, var xStop: Double, var yStop: Double) : ScoreRenderingElement {


    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val xDiff = xStop - xPosition
        val xPoint1 = xPosition + xDiff.div(3.0)
        val xPoint2 = xPosition + xDiff.div(3.0).times(2.0)

        val yDiff = yStop - yPosition
        val yPoint1 = yPosition + yDiff.div(3.0)
        val yPoint2 = yPosition + yDiff.div(3.0).times(2.0)

        val tieElement = PathInterfaceImpl(
                listOf(
                        PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(xPosition.toDouble(), yPosition.toDouble())),
                        PathElement(PathCommand.CURVE_TO_RELATIVE, listOf(xPoint1, yPoint1, xPoint2, yPoint2, xStop, yStop))),
                2, fill = "transparent")

        return listOf(PositionedRenderingElement(listOf(tieElement),
                findBoundingBox(tieElement.pathElements), id, 0, 0))
    }


}