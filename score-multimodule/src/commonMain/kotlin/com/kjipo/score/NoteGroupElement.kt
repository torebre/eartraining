package com.kjipo.score

import com.kjipo.handler.NoteGroup
import com.kjipo.handler.NoteSymbol
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.svg.*
import mu.KotlinLogging
import kotlin.math.absoluteValue


class NoteGroupElement(
    val noteGroup: NoteGroup,
//    val notes: List<NoteSymbol>,
    val context: Context,
    override val properties: Map<String, String> = mapOf()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {
    val result = mutableListOf<PositionedRenderingElementParent>()
    var yLowestPosition = Int.MAX_VALUE
    var yHighestPosition = Int.MIN_VALUE

    private val highlightElements = mutableSetOf<String>()

    override val id: String = noteGroup.id
    // TODO Handle duration on note level
    override var duration: Duration = noteGroup.notes.first().duration

    private val logger = KotlinLogging.logger {}


    override fun toRenderingElement(): List<PositionedRenderingElementParent> {
        return result
    }


    private fun addExtraBarLines(noteSymbol: NoteSymbol) =
        addExtraBarLinesForGClef(
            getNoteWithoutAccidental(noteSymbol.noteType), noteSymbol.octave,
            translation?.xShift ?: 0,
            translation?.yShift ?: 0,
            -25,
            35,
            context.getAndIncrementExtraBarLinesCounter()
        )?.also { scoreRenderingElement -> scoreRenderingElement.translation = translation }


    fun layoutNoteHeads() {
        for (note in noteGroup.notes) {
            val yPositionForNoteHead = calculateVerticalOffset(getNoteWithoutAccidental(note.noteType), note.octave)
            val noteHeadTranslation =
                translation?.let { Translation(it.xShift, it.yShift + yPositionForNoteHead) } ?: Translation(
                    0,
                    yPositionForNoteHead
                )

            if (noteRequiresSharp(note.noteType)) {
                result.add(
                    setupAccidental(
                        "${note.id}-sharp",
                        Accidental.SHARP,
                        noteHeadTranslation
                    )
                )
            }

            val glyphData = getNoteHeadGlyph(duration)
            PositionedRenderingElement.create(
                glyphData.boundingBox,
                context.getAndIncrementIdCounter(),
                noteHeadTranslation,
                duration.name,
                true
            ).also {
                result.add(it)
                highlightElements.add(it.id)
            }

            if (yLowestPosition > yPositionForNoteHead) {
                yLowestPosition = yPositionForNoteHead
            }

            if (yHighestPosition < yPositionForNoteHead) {
                yHighestPosition = yPositionForNoteHead
            }
        }

        noteGroup.notes.maxWithOrNull { a, b ->
            getPitch(a.noteType, a.octave).compareTo(getPitch(b.noteType, b.octave))
        }?.let { noteSymbol ->
            addExtraBarLines(noteSymbol)?.let {
                result.addAll(it.toRenderingElement())
            }
        }

        noteGroup.notes.minWithOrNull { a, b ->
            getPitch(a.noteType, a.octave).compareTo(getPitch(b.noteType, b.octave))
        }?.let { noteSymbol ->
            addExtraBarLines(noteSymbol)?.let {
                result.addAll(it.toRenderingElement())
            }
        }

        noteGroup.stem?.let {
            result.add(addStem(it == Stem.UP))
        }

    }

    private fun getStem(
        xCoordinate: Int,
        yCoordinate: Int,
        stemHeight: Int,
        stemUp: Boolean
    ): PositionedRenderingElement {
        val stem = addStem(xCoordinate, yCoordinate, DEFAULT_STEM_WIDTH, stemHeight, stemUp)

        return TranslatedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            context.getAndIncrementStemCounter(),
            null,
            translation ?: Translation(0, 0)
        )
    }

    override fun getGlyphs(): Map<String, GlyphData> {
        val glyphsUsed = mutableMapOf<String, GlyphData>()

//        if (requiresStem()) {
//            // Use the bounding box for the note head of a half note to determine
//            // how far to move the stem so that it is on the right side of the note head
//            val stem = addStem(getNoteHeadGlyph(Duration.HALF).boundingBox)
//
//            glyphsUsed[STEM_UP] = GlyphData(STEM_UP, stem.pathElements, findBoundingBox(stem.pathElements))
//        }

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


    fun addStem(stemUp: Boolean): PositionedRenderingElement {
        val xCoordinate = getRightEdgeOfNoteHeadGlyph()
        // Not setting stemElement.typeId to avoid references being used, the stem is created specifically for this note group
        val ySpanForNoteGroup = yHighestPosition.minus(yLowestPosition).absoluteValue
        return getStem(xCoordinate, yHighestPosition, ySpanForNoteGroup + DEFAULT_STEM_HEIGHT, stemUp)
    }

    private fun getRightEdgeOfNoteHeadGlyph() = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()

    override fun getIdsOfHighlightElements(): Collection<String> {
        return highlightElements
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

        private fun getNoteWithoutAccidental(noteType: NoteType): GClefNoteLine {
            return when (noteType) {
                NoteType.A_SHARP -> GClefNoteLine.A
                NoteType.A -> GClefNoteLine.A
                NoteType.H -> GClefNoteLine.H
                NoteType.C -> GClefNoteLine.C
                NoteType.C_SHARP -> GClefNoteLine.C
                NoteType.D -> GClefNoteLine.D
                NoteType.D_SHARP -> GClefNoteLine.D
                NoteType.E -> GClefNoteLine.E
                NoteType.F -> GClefNoteLine.F
                NoteType.F_SHARP -> GClefNoteLine.F
                NoteType.G -> GClefNoteLine.G
                NoteType.G_SHARP -> GClefNoteLine.G
            }

        }

        fun noteRequiresSharp(noteType: NoteType): Boolean {
            return when (noteType) {
                NoteType.A_SHARP, NoteType.C_SHARP, NoteType.D_SHARP, NoteType.F_SHARP, NoteType.G_SHARP -> true
                else -> false
            }
        }

    }

}
