package com.kjipo.svg

import com.kjipo.font.GlyphFactory
import com.kjipo.font.NoteType

class NoteElement(val pitch: Int,
                  val duration: Int,
                  override var xPosition: Int,
                  override var yPosition: Int,
                  override val beamGroup: Int) : ScoreRenderingElement, Stemable {
    var bar: BAR? = null


    override fun toRenderingElement(): PositionedRenderingElement {
        return when(duration) {
        // TODO Fix duration values. Should not be hardcoded here
            24 -> GlyphFactory.getGlyph(NoteType.QUARTER_NOTE)
                    .let { RenderingElementImpl(listOf(it), it.boundingBox) }
            48 -> GlyphFactory.getGlyph(NoteType.HALF_NOTE)
                    .let { RenderingElementImpl(listOf(it), it.boundingBox) }
            else -> throw IllegalArgumentException("Unhandled duration: ${duration}")
        }
    }

    fun getClef(): Clef {
        // TODO The clef can change withing a bar. This is not handled at present
        // Defaulting to G
        return bar?.clef ?: Clef.G
    }

}