package com.kjipo.svg

import com.kjipo.font.BoundingBox
import com.kjipo.font.PathInterface

class StemElement(override var xPosition: Int,
                  override var yPosition: Int,
                  override val renderingPath: List<PathInterface>,
                  override val boundingBox: BoundingBox,
                  val noteElement: NoteElement) : PositionedRenderingElement {
    override var id = -1
}

class BeamElement(override var xPosition: Int,
                  override var yPosition: Int,
                  override val renderingPath: List<PathInterface>,
                  override val boundingBox: BoundingBox) : PositionedRenderingElement {
    override var id = -1
}
