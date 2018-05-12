package com.kjipo.svg

import com.kjipo.font.GlyphFactory
import com.kjipo.font.NoteType

class NoteElement(val note: com.kjipo.svg.NoteType,
                  val octave: Int,
                  val duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  override val beamGroup: Int) : ScoreRenderingElement, Stemable {
    var bar: BAR? = null

    override fun toRenderingElement(): PositionedRenderingElement {
        val noteRenderedElement = when (duration) {
        // TODO Fix duration values. Should not be hardcoded here
            Duration.QUARTER -> GlyphFactory.getGlyph(NoteType.QUARTER_NOTE)
                    .let { RenderingElementImpl(it) }
            Duration.HALF -> GlyphFactory.getGlyph(NoteType.HALF_NOTE)
                    .let { RenderingElementImpl(it) }
            else -> throw IllegalArgumentException("Unhandled duration: ${duration}")
        }

        noteRenderedElement.xPosition = xPosition
        noteRenderedElement.yPosition = yPosition

        return noteRenderedElement
    }

    fun requiresStem(): Boolean {
        // TODO Make proper computation
        return duration == Duration.HALF || duration == Duration.QUARTER
    }

    fun getClef(): Clef {
        // TODO The clef can change withing a bar. This is not handled at present
        // Defaulting to G
        return bar?.clef ?: Clef.G
    }

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, xPosition=$xPosition, yPosition=$yPosition, beamGroup=$beamGroup, bar=$bar)"
    }


}