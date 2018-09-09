package com.kjipo.score

import com.kjipo.svg.PathInterfaceImpl
import com.kjipo.svg.getGlyph


class NoteElement(var note: NoteType,
                  var octave: Int,
                  override var duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  val beamGroup: Int,
                  override val id: String) : ScoreRenderingElement, TemporalElement {

    override fun toRenderingElement(): PositionedRenderingElement {
        val glyphData = getGlyph(duration)
        return PositionedRenderingElement.create(listOf(PathInterfaceImpl(glyphData.pathElements, 1)), glyphData.boundingBox, id,
                xPosition,
                yPosition)
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