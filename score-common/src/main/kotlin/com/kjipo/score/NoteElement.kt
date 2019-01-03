package com.kjipo.score

import com.kjipo.svg.*


class NoteElement(var note: NoteType,
                  var octave: Int,
                  override var duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  val beamGroup: Int,
                  override val id: String,
                  val tie: String? = null) : ScoreRenderingElement, TemporalElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val glyphData = getGlyph(duration)
        return PositionedRenderingElement.create(listOf(PathInterfaceImpl(glyphData.pathElements, 1)), glyphData.boundingBox, id,
                xPosition,
                yPosition,
                // TODO This is only here to test the translation functionality
                transform = Translation(100, 100))
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

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, xPosition=$xPosition, yPosition=$yPosition, beamGroup=$beamGroup, id='$id')"
    }


}


class TieElement(val id: String, override var xPosition: Int,
                 override var yPosition: Int, var xStop: Double, var yStop: Double) : ScoreRenderingElement {


    override fun toRenderingElement(): PositionedRenderingElement {
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

        return PositionedRenderingElement(listOf(tieElement),
                findBoundingBox(tieElement.pathElements), id, 0, 0)
    }


}