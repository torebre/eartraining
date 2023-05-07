package com.kjipo.score

import com.kjipo.handler.NoteGroup
import com.kjipo.handler.NoteSymbol
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.svg.*
import mu.KotlinLogging


class NoteGroupElement(
    val noteGroup: NoteGroup,
    context: Context,
) : AbstractNoteElement(context) {
    override val id: String = noteGroup.id

    // TODO Handle duration on note level
    override var duration: Duration = noteGroup.notes.first().duration

    private val logger = KotlinLogging.logger {}


    private fun addExtraBarLines(noteSymbol: NoteSymbol) =
        addExtraBarLinesForGClef(
            getNoteWithoutAccidental(noteSymbol.noteType),
            noteSymbol.octave,
            translation?.xShift ?: 0.0,
            translation?.yShift ?: 0.0,
            -25,
            35,
            context.getAndIncrementExtraBarLinesCounter()
        )?.also { scoreRenderingElement -> scoreRenderingElement.translation = translation }


    override fun doLayout(pixelsPerTick: Double) {
        val accidentals = mutableListOf<TranslatedRenderingElementUsingReference>()

        for (note in noteGroup.notes) {
            val yPositionForNoteHead = calculateVerticalOffset(getNoteWithoutAccidental(note.noteType), note.octave)
            val noteHeadTranslation =
                translation?.let { Translation(it.xShift, it.yShift + yPositionForNoteHead) } ?: Translation(
                    0.0,
                    yPositionForNoteHead
                )

            if (noteRequiresSharp(note.noteType)) {
                setupAccidental(
                    "${note.id}-sharp",
                    Accidental.SHARP,
                    noteHeadTranslation
                ).also { accidentals.add(it) }
                    .also { positionedRenderingElements.add(it) }
            }

            val glyphData = getNoteHeadGlyph(duration)
            PositionedRenderingElement.create(
                glyphData.boundingBox,
                context.getAndIncrementIdCounter(),
                noteHeadTranslation,
                duration.name,
                true
            ).also {
                positionedRenderingElements.add(it)
                highlightElements.add(it.id)
            }
        }

        handleOverlappingAccidentals(accidentals)

        noteGroup.notes.maxWithOrNull { a, b ->
            getPitch(a.noteType, a.octave).compareTo(getPitch(b.noteType, b.octave))
        }?.let { noteSymbol ->
            addExtraBarLines(noteSymbol)?.let {
                positionedRenderingElements.addAll(it.toRenderingElement())
            }
        }

        noteGroup.notes.minWithOrNull { a, b ->
            getPitch(a.noteType, a.octave).compareTo(getPitch(b.noteType, b.octave))
        }?.let { noteSymbol ->
            addExtraBarLines(noteSymbol)?.let {
                positionedRenderingElements.addAll(it.toRenderingElement())
            }
        }

        if (noteGroup.stem != Stem.NONE) {
            translatedStemElement = addStem()
        }

    }

    private fun handleOverlappingAccidentals(accidentals: MutableList<TranslatedRenderingElementUsingReference>) {
        if (accidentals.isNotEmpty()) {
            val positionedBoundingBoxes = mutableListOf<BoundingBox>()
            val positionedBoundingBoxAccidentalMap =
                mutableMapOf<BoundingBox, PositionedRenderingElementParent>()
            for (accidental in accidentals) {
                val positionedBoundingBox = BoundingBox(
                    accidental.translation.xShift + accidental.boundingBox.xMin,
                    accidental.translation.yShift + accidental.boundingBox.yMin,
                    accidental.translation.xShift + accidental.boundingBox.xMax,
                    accidental.translation.yShift + accidental.boundingBox.yMax
                )
                positionedBoundingBoxes.add(positionedBoundingBox)
                positionedBoundingBoxAccidentalMap[positionedBoundingBox] = accidental
            }

            positionedBoundingBoxes.sortBy { it.yMax }
            for ((index, positionedBoundingBox) in positionedBoundingBoxes.withIndex()) {
                if (index != positionedBoundingBoxes.size - 1) {
                    val positionedBoundingBoxNext = positionedBoundingBoxes[index + 1]

                    // TODO Need to take x-coordinates into consideration too it is not necessary to move all sharps that overlap on the y-axis
                    if (positionedBoundingBox.yMin < positionedBoundingBoxNext.yMax) {
                        val accidental = positionedBoundingBoxAccidentalMap[positionedBoundingBoxNext]
                        val indexAccidental = positionedRenderingElements.indexOf(accidental)

                        positionedRenderingElements.removeAt(indexAccidental)
                        with(accidental as TranslatedRenderingElementUsingReference) {
                            positionedRenderingElements.add(
                                indexAccidental,
                                copy(
                                    translation = Translation(
                                        accidental.translation.xShift - 50,
                                        accidental.translation.yShift
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getStem(
        xCoordinate: Double,
        yCoordinate: Double,
        stemHeight: Double,
        stemUp: Boolean
    ): TranslatedRenderingElement {
        val stem = addStem(xCoordinate, yCoordinate, DEFAULT_STEM_WIDTH, stemHeight, stemUp)

        return TranslatedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            context.getAndIncrementStemCounter(),
            null,
            translation ?: Translation(0.0, 0.0)
        )
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

        if (sharpInUse()) {
            glyphsUsed.put(Accidental.SHARP.name, getAccidentalGlyph(Accidental.SHARP))
        }

        if (duration == Duration.EIGHT) {
            getFlagGlyph(Duration.EIGHT, noteGroup.stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }


    private fun sharpInUse() = noteGroup.notes.map {
        noteRequiresSharp(it.noteType)
    }.any { it }


    override fun updateStemHeight(stemHeight: Double) {
        this.stemHeightInternal = stemHeight
        translatedStemElement = addStem()
    }

    override fun isStemUp(): Boolean {
        return this.noteGroup.stem == Stem.UP
    }

    private fun addStem(): TranslatedRenderingElement? {
        if (noteGroup.stem == Stem.NONE) {
            return null
        }

        val stemUp = isStemUp()
        val xCoordinate = getXTranslateForStem()

        // Not setting stemElement.typeId to avoid references being used, the stem is created specifically for this note group
        return getStem(
            xCoordinate,
            getVerticalOffsetForStemStart(),
            stemHeightInternal,
            stemUp
        )
    }

    private fun getXTranslateForStem(): Double {
        return if (isStemUp()) {
            getRightEdgeOfNoteHeadGlyph()
        } else {
            getLeftEdgeOfNoteHeadGlyph() + STEM_UP_STEM_X_OFFSET
        }
    }

    private fun getRightEdgeOfNoteHeadGlyph() = getNoteHeadGlyph(duration).boundingBox.xMax

    private fun getLeftEdgeOfNoteHeadGlyph() = getNoteHeadGlyph(duration).boundingBox.xMin

    override fun getTieCoordinates(top: Boolean): Pair<Double, Double> {
        TODO("Not yet implemented")
    }

    override fun getAbsoluteCoordinatesForEndpointOfStem(): Pair<Double, Double>? {
        val stemHeight = when (noteGroup.stem) {
            Stem.UP -> -stemHeightInternal
            Stem.DOWN -> stemHeightInternal
            Stem.NONE -> return null
        }
        val xCoord = (translation?.xShift ?: 0.0) + getXTranslateForStem()
        val yCoord = (translation?.yShift ?: 0.0) + getVerticalOffsetForStemStart() + stemHeight

        return Pair(xCoord, yCoord)
    }

    override fun getVerticalOffsetForStemStart(): Double {
        return noteGroup.notes.maxOfOrNull { note ->
            calculateVerticalOffset(getNoteWithoutAccidental(note.noteType), note.octave)
        } ?: 0.0
    }


    companion object {

        private fun setupAccidental(
            id: String,
            accidental: Accidental,
            inputTranslation: Translation
        ): TranslatedRenderingElementUsingReference {
            return getAccidentalGlyph(accidental).let { accidentalGlyph ->
                PositionedRenderingElement.create(
                    accidentalGlyph.boundingBox,
                    id,
                    inputTranslation.let {
                        Translation(it.xShift - 30, it.yShift)
                    }, accidental.name,
                    true
                )
            }
        }
    }

}
