package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import kotlinx.serialization.Serializable


@Serializable
class PositionedRenderingElement(val renderingPath: List<PathInterfaceImpl>,
                                 val boundingBox: BoundingBox,
                                 var id: String,
                                 val groupClass: String? = null) {

    var glyphData: GlyphData? = null
    var typeId: String? = null
    var xTranslate = 0
    var yTranslate = 0

    var xPosition: Int = 0
    var yPosition: Int = 0

//    @Transient
//    var renderGroup: RenderGroup? = null


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
                    boundingBox, id).also {
                it.xPosition = xPosition
                it.yPosition = yPosition
            }
        }

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PositionedRenderingElement

        if (renderingPath != other.renderingPath) return false
        if (boundingBox != other.boundingBox) return false
        if (id != other.id) return false
        if (groupClass != other.groupClass) return false
        if (glyphData != other.glyphData) return false
        if (typeId != other.typeId) return false
        if (xTranslate != other.xTranslate) return false
        if (yTranslate != other.yTranslate) return false
        if (xPosition != other.xPosition) return false
        if (yPosition != other.yPosition) return false
//        if (renderGroup != other.renderGroup) return false

        return true
    }

    override fun hashCode(): Int {
        var result = renderingPath.hashCode()
        result = 31 * result + boundingBox.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (groupClass?.hashCode() ?: 0)
        result = 31 * result + (glyphData?.hashCode() ?: 0)
        result = 31 * result + (typeId?.hashCode() ?: 0)
        result = 31 * result + xTranslate
        result = 31 * result + yTranslate
        result = 31 * result + xPosition
        result = 31 * result + yPosition
//        result = 31 * result + (renderGroup?.hashCode() ?: 0)
        return result
    }


    override fun toString(): String {
        return "PositionedRenderingElement(renderingPath=$renderingPath, boundingBox=$boundingBox, id='$id', glyphData=$glyphData, typeId=$typeId, xTranslate=$xTranslate, yTranslate=$yTranslate)"
    }


}
