package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterface

data class RenderingElementImpl(override val glyphData: GlyphData?,
                                override val renderingPath: List<PathInterface>,
                                override val boundingBox: BoundingBox,
                                override val id: String,
                                val beamGroup: Int = 0) : PositionedRenderingElement {

    override var xPosition = 0
    override var yPosition = 0


    constructor (renderingPath: List<PathInterface>,
                 boundingBox: BoundingBox,
                 id: String,
                 beamGroup: Int = 0) : this(null, renderingPath, boundingBox, id, beamGroup)

    constructor(glyphData: GlyphData, id: String) : this(glyphData, listOf(glyphData), glyphData.boundingBox, id)

}

interface Stemable {
    val beamGroup: Int

}
