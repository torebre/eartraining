package com.kjipo.score

import com.kjipo.handler.Note
import com.kjipo.handler.ScoreHelperFunctions.transformToNoteAndAccidental
import com.kjipo.svg.*
import mu.KotlinLogging


/**
 * Knows how to generate a layout for the graphical elements belonging to a note.
 */
class NoteElement(
    val note: Note,
    context: Context,
) : AbstractNoteElement(context) {
    override var duration: Duration = note.duration
    override val id: String = note.id

    private var noteHead: TranslatedRenderingElementUsingReference? = null

    private val logger = KotlinLogging.logger {}

    override fun doLayout(pixelsPerTick: Double) {
        layoutElements()

        val debugBox = determineDebugBox(
            "${note.id}_debug_box",
            positionedRenderingElements
        )

        internalShiftX = (note.duration.ticks * pixelsPerTick - debugBox.width) / 2.0
        internalShiftY = calculateVerticalOffset(
            transformToNoteAndAccidental(note.noteType).first,
            note.octave
        )

        positionedRenderingElements.clear()
        highlightElements.clear()
        layoutElements()

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

    private fun layoutElements() {
        note.accidental?.run {
            positionedRenderingElements.add(setupAccidental(this))
        }

        translatedStemElement = calculateStem()
        // TODO This is a bit brittle. Waiting to add the stem to the list of rendering elements until the list is requested. This is because the stem may change after the first layout calculations. Its height may change because of beams
//        stem?.let { positionedRenderingElements.add(it) }

        setupNoteHeadAndExtraBarLines(getNoteHeadGlyph(duration))

        if (context.shouldNoteFlagBeAdded(id)) {
            setupNoteStemAttachment(duration)?.let { positionedRenderingElements.add(it) }
        }
    }

    private fun setupNoteStemAttachment(duration: Duration): TranslatedRenderingElementUsingReference? {
        return when (duration) {
            Duration.EIGHT -> {
                addEightNoteFlag()
            }

            else -> {
                // The other cases do not need something added to a stem
                null
            }
        }
    }


    private fun addEightNoteFlag(): TranslatedRenderingElementUsingReference {
        val flagGlyph = getFlagGlyph(Duration.EIGHT, isStemUp())
        val shiftX = getTranslationX() + getXTranslateForStem()
        val shiftY = getTranslationY() + getStemHeight() * if(isStemUp()) -1 else 1

        return PositionedRenderingElement.create(
            flagGlyph.boundingBox,
            "$id-flag",
            Translation(shiftX, shiftY),
            flagGlyph.name,
            false
        )
    }

    private fun setupNoteHeadAndExtraBarLines(noteHeadGlyph: GlyphData) {
        noteHead = PositionedRenderingElement.create(
            noteHeadGlyph.boundingBox,
            id,
            getTranslations(),
            duration.name,
            true
        ).also { translatedRenderingElementUsingReference ->
            positionedRenderingElements.add(translatedRenderingElementUsingReference)
            highlightElements.add(translatedRenderingElementUsingReference.id)
        }

        addExtraBarLinesForGClef(
            transformToNoteAndAccidental(note.noteType).first,
            note.octave,
            getTranslationX(),
            getTranslationY(),
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
                getTranslations().let {
                    Translation(it.xShift - 30, it.yShift)
                },
                accidental.name,
                true
            )
        }
    }


    override fun isStemUp(): Boolean {
        return note.stem == Stem.UP
    }

    override fun updateStemHeight(stemHeight: Double) {
        this.stemHeightInternal = stemHeight
        translatedStemElement = calculateStem()
    }

    override fun getAbsoluteCoordinatesForEndpointOfStem(): Pair<Double, Double>? {
        val xCoord = getXTranslateForStem()
        val yCoord = when (note.stem) {
            Stem.UP -> -stemHeightInternal
            Stem.DOWN -> stemHeightInternal
            Stem.NONE -> null
        }

        return if (yCoord != null) {
            getTranslations().let { Pair(it.xShift + xCoord, it.yShift + yCoord) }
        } else {
            null
        }
    }


    private fun calculateStem(): TranslatedRenderingElement? {
        if (note.stem == Stem.NONE) {
            return null
        }

        val xTranslateForStem = getXTranslateForStem()
        val stem = addStem(xTranslateForStem, 0.0, DEFAULT_STEM_WIDTH, stemHeightInternal, note.stem != Stem.DOWN)

        return TranslatedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements), context.getAndIncrementStemCounter(),
            null, getTranslations()
        ).also { stemElement ->
            // Add element ID so that the stem is tied to the note element
            properties.getProperty(ELEMENT_ID)?.let {
                stemElement.properties[ELEMENT_ID] = it
            }
        }
    }

    /**
     * This is the translation to move a stem pointing upwards to the right of the note head.
     */
    private fun getXTranslateForStem() = if (note.stem == Stem.UP) {
        // If the stem is pointing up then it should be moved to the
        // right side of the note head
        getNoteHeadGlyph(duration).boundingBox.xMax
    } else {
        0.0
    }

    override fun getVerticalOffsetForStemStart(): Double {
        return internalShiftY
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

        note.accidental?.let {
            glyphsUsed.put(it.name, getAccidentalGlyph(it))
        }

        if (duration == Duration.EIGHT && context.shouldNoteFlagBeAdded(id)) {
            getFlagGlyph(Duration.EIGHT, note.stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }

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

    private fun getTranslationX() = (translation?.xShift ?: 0.0) + internalShiftX

    private fun getTranslationY() = (translation?.yShift ?: 0.0) + internalShiftY

    private fun getTranslations() = Translation(getTranslationX(), getTranslationY())


    override fun toString(): String {
        return "NoteElement(note=$note, translation=$translation)"
    }

}
