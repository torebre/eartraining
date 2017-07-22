package com.kjipo.svg

import com.kjipo.font.BoundingBox
import com.kjipo.font.GlyphData
import com.kjipo.font.PathInterface

data class RenderingElementImpl(override val renderingPath: List<PathInterface>, override val boundingBox: BoundingBox) : PositionedRenderingElement {
    override var xPosition = 0
    override var yPosition = 0

    constructor(glyphData: GlyphData) : this(listOf(glyphData), glyphData.boundingBox)

    constructor(renderingElement1: RenderingElementImpl, renderingElement2: RenderingElementImpl) :
            this(renderingElement1.renderingPath.plus(renderingElement2.renderingPath),
                    mergeBoundingBoxes(renderingElement1.boundingBox, renderingElement2.boundingBox))

}


private fun mergeBoundingBoxes(box1: BoundingBox, box2: BoundingBox): BoundingBox {
    return BoundingBox(Math.min(box1.xMin, box2.xMin),
            Math.min(box1.yMin, box2.yMin),
            Math.max(box1.xMax, box2.xMax),
            Math.max(box1.yMax, box2.yMin))
}