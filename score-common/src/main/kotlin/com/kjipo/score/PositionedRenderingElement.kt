package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import kotlinx.serialization.Serializable


@Serializable
class PositionedRenderingElement(val renderingPath: List<PathInterfaceImpl>,
                                 val boundingBox: BoundingBox,
                                 var id: String,
                                 var xPosition: Int,
                                 var yPosition: Int) {

    var glyphData: GlyphData? = null
    var duration : Duration? = null


    companion object {

        fun create(glyphData: GlyphData, id: String): PositionedRenderingElement {
            return PositionedRenderingElement(
                    listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                    glyphData.boundingBox, id, 0, 0)
        }

        fun create(renderingPath: List<PathInterfaceImpl>,
                   boundingBox: BoundingBox,
                   id: String,
                   xPosition: Int,
                   yPosition: Int): PositionedRenderingElement {
            return PositionedRenderingElement(
                    renderingPath,
                    boundingBox, id,
                    xPosition,
                    yPosition)
        }


    }

    override fun toString(): String {
        return "PositionedRenderingElement(renderingPath=$renderingPath, boundingBox=$boundingBox, id='$id', xPosition=$xPosition, yPosition=$yPosition, glyphData=$glyphData)"
    }


}
