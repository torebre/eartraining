package com.kjipo.score

import com.kjipo.handler.NoteSymbol
import com.kjipo.handler.ScoreHandlerUtilities.getPitch
import com.kjipo.svg.*
import kotlin.math.absoluteValue


class NoteGroupElement(
    val notes: List<NoteSymbol>,
    override var duration: Duration,
    override val id: String,
    val context: Context,
    override val properties: Map<String, String> = mapOf()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {
    val result = mutableListOf<PositionedRenderingElement>()
    var yLowestPosition = Int.MAX_VALUE
    var yHighestPosition = Int.MIN_VALUE
    private var stem = Stem.NONE

    private val highlightElements = mutableSetOf<String>()


    override fun toRenderingElement(): List<PositionedRenderingElement> {
//        layoutNoteHeads()

        return result
    }


    private fun addExtraBarLines(noteSymbol: NoteSymbol) =
        addExtraBarLinesForGClef(
            getNoteWithoutAccidental(noteSymbol.noteType), noteSymbol.octave,
            0,
            0,
            -25,
            35
        )?.also { scoreRenderingElement -> scoreRenderingElement.translation = translation }


    fun layoutNoteHeads() {
        for (note in notes) {
            val yPositionForNoteHead = calculateVerticalOffset(getNoteWithoutAccidental(note.noteType), note.octave)
            val noteHeadTranslation =
                translation?.let { Translation(it.xShift, it.yShift + yPositionForNoteHead) } ?: Translation(
                    0,
                    yPositionForNoteHead
                )

            if (noteRequiresSharp(note.noteType)) {
                result.add(
                    setupAccidental(
                        id,
                        xPosition,
                        yPositionForNoteHead,
                        Accidental.SHARP,
                        translation
                    )
                )
            }

            val glyphData = getNoteHeadGlyph(duration)
            // The x and y positions are not used since references are used for note head elements, so
            // they can be set to 0 without it having any effect
            PositionedRenderingElement.create(
                listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
                glyphData.boundingBox,
                context.getAndIncrementIdCounter(),
                0,
                0,
                noteHeadTranslation
            ).also {
                it.typeId = duration.name
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

        notes.maxWithOrNull { a, b ->
            getPitch(a.noteType, a.octave).compareTo(getPitch(b.noteType, b.octave))
        }?.let { noteSymbol ->
            addExtraBarLines(noteSymbol)?.let {
                result.addAll(it.toRenderingElement())
            }
        }

        notes.minWithOrNull { a, b ->
            getPitch(a.noteType, a.octave).compareTo(getPitch(b.noteType, b.octave))
        }?.let { noteSymbol ->
            addExtraBarLines(noteSymbol)?.let {
                result.addAll(it.toRenderingElement())
            }
        }
    }


    private fun getStem(xCoordinate: Int, yCoordinate: Int, stemHeight: Int): PositionedRenderingElement {
        val stem = addStem(xCoordinate, yCoordinate, DEFAULT_STEM_WIDTH, stemHeight, stem != Stem.DOWN)

        return PositionedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            context.getAndIncrementStemCounter(), translation = translation
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
            getFlagGlyph(Duration.EIGHT, stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }


    private fun sharpInUse() = notes.map {
        noteRequiresSharp(it.noteType)
    }.any { it }


    fun addStem(stem: Stem) {
        this.stem = stem
        val xCoordinate = getRightEdgeOfNoteHeadGlyph()
        // Not setting stemElement.typeId to avoid references being used, the stem is created specifically for this note group
        val ySpanForNoteGroup = yHighestPosition.minus(yLowestPosition).absoluteValue
        val stemElement = getStem(xCoordinate, yHighestPosition, ySpanForNoteGroup + DEFAULT_STEM_HEIGHT)
        result.add(stemElement)
    }

    private fun getRightEdgeOfNoteHeadGlyph() = getNoteHeadGlyph(duration).boundingBox.xMax.toInt()

    override fun getIdsOfHighlightElements(): Collection<String> {
        return highlightElements
    }


    companion object {

        private fun setupAccidental(
            id: String,
            xPosition: Int,
            yPosition: Int,
            accidental: Accidental,
            inputTranslation: Translation?
        ): PositionedRenderingElement {
            val accidentalGlyph = getAccidentalGlyph(accidental)
            return PositionedRenderingElement.create(
                listOf(PathInterfaceImpl(accidentalGlyph.pathElements, 1)), accidentalGlyph.boundingBox, id,
                xPosition - 30,
                yPosition,
                inputTranslation
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
