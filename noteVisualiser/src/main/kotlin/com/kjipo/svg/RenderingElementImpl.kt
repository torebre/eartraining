package com.kjipo.svg

import com.kjipo.font.BoundingBox
import com.kjipo.font.GlyphData
import com.kjipo.font.PathInterface

data class RenderingElementImpl(override val renderingPath: List<PathInterface>,
                                override val boundingBox: BoundingBox,
                                val beamGroup: Int = 0) : PositionedRenderingElement {
    override var id = -1
    override var xPosition = 0
    override var yPosition = 0

    constructor(glyphData: GlyphData) : this(listOf(glyphData), glyphData.boundingBox)

}

interface Stemable {
    val beamGroup: Int

}
