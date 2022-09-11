package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterfaceImpl
import kotlinx.serialization.Serializable


@Serializable
sealed class PositionedRenderingElementParent {
    abstract val id: String
    abstract val groupClass: String?
    abstract val isClickable: Boolean
    abstract val boundingBox: BoundingBox

    val properties = mutableMapOf<String, String>()
}

@Serializable
sealed class PositionedRenderingElement : PositionedRenderingElementParent() {
    abstract val renderingPath: List<PathInterfaceImpl>


    companion object {

        fun create(glyphData: GlyphData, id: String): TranslatedRenderingElement {
            // TODO
            return TranslatedRenderingElement(
                listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox, id, null, Translation(0.0, 0.0)
            )
        }

        fun create(
            renderingPath: List<PathInterfaceImpl>,
            boundingBox: BoundingBox,
            id: String,
            translation: Translation,
            isClickable: Boolean
        ): TranslatedRenderingElement {
            return TranslatedRenderingElement(
                renderingPath,
                boundingBox,
                id,
                null,
                translation,
                isClickable
            )
        }


        fun create(
            boundingBox: BoundingBox,
            id: String,
            translation: Translation,
            typeId: String,
            isClickable: Boolean
        ): TranslatedRenderingElementUsingReference {
            return TranslatedRenderingElementUsingReference(
                id,
                null,
                translation,
                typeId,
                isClickable,
                boundingBox
            )
        }
    }

}

@Serializable
class AbsolutelyPositionedRenderingElement(
    override val renderingPath: List<PathInterfaceImpl>,
    override val boundingBox: BoundingBox,
    override val id: String,
    override val groupClass: String? = null,
    override val isClickable: Boolean = false
) : PositionedRenderingElement()


@Serializable
class TranslatedRenderingElement(
    override val renderingPath: List<PathInterfaceImpl>,
    override val boundingBox: BoundingBox,
    override val id: String,
    override val groupClass: String? = null,
    var translation: Translation,
    override val isClickable: Boolean = false,
) : PositionedRenderingElement()


@Serializable
data class TranslatedRenderingElementUsingReference(
    override val id: String,
    override val groupClass: String?,
    val translation: Translation,
    val typeId: String,
    override val isClickable: Boolean = false,
    override val boundingBox: BoundingBox
) : PositionedRenderingElementParent()