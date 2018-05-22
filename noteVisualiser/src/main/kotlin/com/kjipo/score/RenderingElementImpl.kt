package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterface

data class RenderingElementImpl(override val glyphData: GlyphData?,
                                override val renderingPath: List<PathInterface>,
                                override val boundingBox: BoundingBox,
                                val beamGroup: Int = 0) : PositionedRenderingElement {
    override var id = -1
    override var xPosition = 0
    override var yPosition = 0


    constructor (renderingPath: List<PathInterface>,
                 boundingBox: BoundingBox,
                 beamGroup: Int = 0) : this(null, renderingPath, boundingBox, beamGroup)

    constructor(glyphData: GlyphData,
                boundingBox: BoundingBox,
                beamGroup: Int = 0) : this (glyphData, emptyList(), boundingBox, beamGroup)

    constructor(glyphData: GlyphData) : this(glyphData, listOf(glyphData), glyphData.boundingBox)

}

interface Stemable {
    val beamGroup: Int

}