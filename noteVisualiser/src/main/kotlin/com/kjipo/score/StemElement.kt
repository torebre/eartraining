package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterface

class StemElement(override var xPosition: Int,
                  override var yPosition: Int,
                  override val renderingPath: List<PathInterface>,
                  override val boundingBox: BoundingBox,
                  val noteElement: NoteElement) : PositionedRenderingElement {
    override val glyphData: GlyphData? = null
    override var id = -1
}

class BeamElement(override var xPosition: Int,
                  override var yPosition: Int,
                  override val renderingPath: List<PathInterface>,
                  override val boundingBox: BoundingBox) : PositionedRenderingElement {
    override val glyphData: GlyphData? = null
    override var id = -1
}
