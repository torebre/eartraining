package com.kjipo.score

/**
 * Collection of variables used by NoteElement and NoteGroupElement
 */
abstract class AbstractNoteElement(
    protected val context: Context,
    val properties: ElementWithProperties = Properties()
) :
    ScoreRenderingElement(),
    TemporalElement,
    HighlightableElement,
    ElementCanBeTied,
    ElementCanBeInBeamGroup,
    ElementWithProperties by properties {
    protected var internalShiftX = 0.0
    protected var internalShiftY = 0.0

    protected var translatedStemElement: TranslatedRenderingElement? = null
    protected var stemHeightInternal = DEFAULT_STEM_HEIGHT

    protected val highlightElements = mutableSetOf<String>()
    protected val positionedRenderingElements = mutableListOf<PositionedRenderingElementParent>()


    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        return positionedRenderingElements + translatedStemElement.let { if (it == null) emptyList() else listOf(it) }
    }

    override fun getIdsOfHighlightElements() = highlightElements

    override fun getStem() = translatedStemElement

    override fun getStemHeight(): Double {
        return stemHeightInternal
    }


}