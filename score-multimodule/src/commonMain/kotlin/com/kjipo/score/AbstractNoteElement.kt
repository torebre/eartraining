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

    protected var stem: TranslatedRenderingElement? = null
    protected var stemHeight = DEFAULT_STEM_HEIGHT

    protected val highlightElements = mutableSetOf<String>()
    protected val positionedRenderingElements = mutableListOf<PositionedRenderingElementParent>()


    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        return positionedRenderingElements + stem.let { if (it == null) emptyList() else listOf(it) }
    }

    override fun getIdsOfHighlightElements() = highlightElements

    override fun getStem() = stem

    override fun getStemHeight(): Double {
        return stemHeight
    }


}