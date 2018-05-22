package com.kjipo.score


import com.kjipo.svg.GlyphFactory

class NoteElement(val note: NoteType,
                  val octave: Int,
                  override val duration: Duration,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  override val beamGroup: Int) : ScoreRenderingElement, Stemable, TemporalElement {
    var bar: BAR? = null

    override fun toRenderingElement(): PositionedRenderingElement {
        val noteRenderedElement = RenderingElementImpl(GlyphFactory.getGlyph(duration))

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