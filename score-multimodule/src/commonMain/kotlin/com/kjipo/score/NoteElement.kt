package com.kjipo.score

import com.kjipo.handler.Note
import com.kjipo.handler.ScoreHelperFunctions.transformToNoteAndAccidental
import com.kjipo.svg.*
import mu.KotlinLogging


class NoteElement(
    val note: Note,
    private val context: Context,
    val properties: ElementWithProperties = Properties()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement, ElementCanBeTied,
    ElementWithProperties by properties {

    override var duration: Duration = note.duration
    override val id: String = note.id

    private val positionedRenderingElements = mutableListOf<PositionedRenderingElementParent>()
    private val highlightElements = mutableSetOf<String>()
    private var noteHead: TranslatedRenderingElementUsingReference? = null


    private val logger = KotlinLogging.logger {}


    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        return positionedRenderingElements
    }

    fun layoutNoteHeads() {
        note.accidental?.run {
            positionedRenderingElements.add(setupAccidental(this))
        }

        setupNoteHeadStemAndExtraBarLines(getNoteHeadGlyph(duration))

        if (context.debug) {
            determineDebugBox(
                "${note.id}_debug_box",
                positionedRenderingElements
            ).let {
                positionedRenderingElements.addAll(it.toRenderingElement())
            }
        }

        properties.getProperty(ELEMENT_ID)?.let { elementId ->
            positionedRenderingElements.forEach { it.properties[ELEMENT_ID] = elementId }
        }
    }

    private fun setupNoteHeadStemAndExtraBarLines(noteHeadGlyph: GlyphData) {
        noteHead = PositionedRenderingElement.create(
            noteHeadGlyph.boundingBox,
            id,
            translation ?: Translation(0.0, 0.0),
            duration.name,
            true
        ).also { translatedRenderingElementUsingReference ->
            positionedRenderingElements.add(translatedRenderingElementUsingReference)
            highlightElements.add(translatedRenderingElementUsingReference.id)
        }

        getStem()?.let { positionedRenderingElements.add(it) }

        addExtraBarLinesForGClef(
            transformToNoteAndAccidental(note.noteType).first,
            note.octave,
            translation?.xShift ?: 0.0,
            translation?.yShift ?: 0.0,
            noteHeadGlyph.boundingBox.xMin.toInt(),
            noteHeadGlyph.boundingBox.xMax.toInt(),
            context.getAndIncrementExtraBarLinesCounter()
        )?.let { extraBarLinesElement ->
            positionedRenderingElements.addAll(extraBarLinesElement.toRenderingElement())
        }
    }

    private fun setupAccidental(accidental: Accidental): PositionedRenderingElementParent {
        return getAccidentalGlyph(accidental).let { accidentalGlyph ->
            PositionedRenderingElement.create(
                accidentalGlyph.boundingBox,
                "$id-acc",
                (translation ?: Translation(0.0, 0.0)).let {
                    Translation(it.xShift - 30, it.yShift)
                },
                accidental.name,
                true
            )
        }
    }

    fun getStem(): PositionedRenderingElement? {
        if (note.stem == Stem.NONE) {
            return null
        }

        val xTranslateForStem = if (note.stem == Stem.UP) {
            // If the stem is pointing up then it should be moved to the
            // right side of the note head
            getNoteHeadGlyph(duration).boundingBox.xMax
        } else {
            0.0
        }

        val stem = addStem(xTranslateForStem, 0.0, DEFAULT_STEM_WIDTH, DEFAULT_STEM_HEIGHT, note.stem != Stem.DOWN)
        return TranslatedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements), context.getAndIncrementStemCounter(),
            null, translation ?: Translation(0.0, 0.0)
        )
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

//        if (stem != Stem.NONE) {
//             Use the bounding box for the note head of a half note to determine
//             how far to move the stem so that it is on the right side of the note head
//            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
//            glyphsUsed[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
//        }

        note.accidental?.let {
            glyphsUsed.put(it.name, getAccidentalGlyph(it))
        }

        if (duration == Duration.EIGHT) {
            getFlagGlyph(Duration.EIGHT, note.stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }

    override fun getIdsOfHighlightElements() = highlightElements
    override fun getTieCoordinates(top: Boolean): Pair<Double, Double> {
        val tieCoordinates = getTieCoordinatesInternal(top)
        if (tieCoordinates == null) {
            logger.error { "Unable to get tie coordinates for element with ID: {id}" }
            return Pair(0.0, 0.0)
        }
        return tieCoordinates
    }

    private fun getTieCoordinatesInternal(top: Boolean): Pair<Double, Double>? {
        return noteHead?.run {
            val xCoord = boundingBox.xMin + (boundingBox.xMax - boundingBox.xMin) / 2
            val yCoord = if (top) {
                boundingBox.yMin
            } else {
                boundingBox.yMax
            }

            Pair(xCoord + translation.xShift, yCoord + translation.yShift)
        }
    }


    override fun toString(): String {
        return "NoteElement(note=$note, translation=$translation)"
    }

}
