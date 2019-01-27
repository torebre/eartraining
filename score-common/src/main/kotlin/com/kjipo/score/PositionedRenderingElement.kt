package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class PositionedRenderingElement(val renderingPath: List<PathInterfaceImpl>,
                                 val boundingBox: BoundingBox,
                                 var id: String) {

    var glyphData: GlyphData? = null
    var typeId: String? = null
    var xTranslate = 0
    var yTranslate = 0

    var xPosition: Int = 0
    var yPosition: Int = 0
    @Transient
    var renderGroup: RenderGroup? = null


    companion object {

        fun create(glyphData: GlyphData, id: String): PositionedRenderingElement {
            return PositionedRenderingElement(
                    listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                    glyphData.boundingBox, id)
        }

        fun create(renderingPath: List<PathInterfaceImpl>,
                   boundingBox: BoundingBox,
                   id: String,
                   xPosition: Int,
                   yPosition: Int): PositionedRenderingElement {
            return PositionedRenderingElement(
                    renderingPath,
                    boundingBox, id).let {
                it.xPosition = xPosition
                it.yPosition = yPosition
                it
            }
        }


    }

    override fun toString(): String {
        return "PositionedRenderingElement(renderingPath=$renderingPath, boundingBox=$boundingBox, id='$id', glyphData=$glyphData, typeId=$typeId, xTranslate=$xTranslate, yTranslate=$yTranslate)"
    }


}
