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

class MultiNoteRenderingElement() : PositionedRenderingElement, Stemable {
    override var id = -1
    override var xPosition = 0
    override var yPosition = 0

    override val renderingPath: List<PathInterface>

    init {
        renderingPath = emptyList()




    }


}


private fun mergeBoundingBoxes(box1: BoundingBox, box2: BoundingBox): BoundingBox {
    return BoundingBox(Math.min(box1.xMin, box2.xMin),
            Math.min(box1.yMin, box2.yMin),
            Math.max(box1.xMax, box2.xMax),
            Math.max(box1.yMax, box2.yMin))
}