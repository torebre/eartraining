package com.kjipo.svg

import com.kjipo.font.BoundingBox
import com.kjipo.font.PathInterface

class StemElement(override var xPosition: Int,
                  override var yPosition: Int,
                  override val renderingPath: List<PathInterface>,
                  override val boundingBox: BoundingBox) : PositionedRenderingElement {
    override var id = -1
    override val tieGroup = 0
    override val stem = null
}

