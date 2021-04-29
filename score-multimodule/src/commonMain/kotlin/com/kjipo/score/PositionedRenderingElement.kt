package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import kotlinx.serialization.Serializable


@Serializable
class PositionedRenderingElement(
    val renderingPath: List<PathInterfaceImpl>,
    val boundingBox: BoundingBox,
    var id: String,
    val groupClass: String? = null,
    var translation: Translation? = null
) {

    var glyphData: GlyphData? = null
    var typeId: String? = null

    var xPosition: Int = 0
    var yPosition: Int = 0


    companion object {

        fun create(glyphData: GlyphData, id: String): PositionedRenderingElement {
            return PositionedRenderingElement(
                listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox, id
            )
        }

        fun create(
            renderingPath: List<PathInterfaceImpl>,
            boundingBox: BoundingBox,
            id: String,
            xPosition: Int,
            yPosition: Int,
            translation: Translation?
        ): PositionedRenderingElement {
            return PositionedRenderingElement(
                renderingPath,
                boundingBox, id, translation = translation
            ).also {
                it.xPosition = xPosition
                it.yPosition = yPosition
            }
        }

    }

    override fun toString(): String {
        return "PositionedRenderingElement(renderingPath=$renderingPath, boundingBox=$boundingBox, id='$id', glyphData=$glyphData, typeId=$typeId, translate=$translation"
    }

}
