package com.kjipo.score

import com.kjipo.handler.Note
import com.kjipo.handler.ScoreHelperFunctions.transformToNoteAndAccidental
import com.kjipo.svg.*
import mu.KotlinLogging


class NoteElement(
    val note: Note,
    val context: Context,
    override val properties: Map<String, String> = mapOf(),
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {

    override var duration: Duration = note.duration
    override val id: String = note.id

    val positionedRenderingElements = mutableListOf<PositionedRenderingElementParent>()

    private val highlightElements = mutableSetOf<String>()

    private val logger = KotlinLogging.logger {}


    override fun toRenderingElement() = positionedRenderingElements

    fun layoutNoteHeads() {
        note.accidental?.run {
            positionedRenderingElements.add(setupAccidental(this))
        }

        getNoteHeadGlyph(duration).let { noteHeadGlyph ->
            PositionedRenderingElement.create(
                noteHeadGlyph.boundingBox,
                id,
                translation ?: Translation(0, 0),
                duration.name,
                true
            ).let {
                positionedRenderingElements.add(it)
                highlightElements.add(it.id)
            }

            note.stem?.run {
                positionedRenderingElements.add(getStem())
            }

            addExtraBarLinesForGClef(
                transformToNoteAndAccidental(note.noteType).first,
                note.octave,
                translation?.xShift ?: 0,
                translation?.yShift ?: 0,
                noteHeadGlyph.boundingBox.xMin.toInt(),
                noteHeadGlyph.boundingBox.xMax.toInt(),
                context.getAndIncrementExtraBarLinesCounter()
            )?.let { extraBarLinesElement ->

                logger.debug { "Extra bar lines element translation: ${extraBarLinesElement.translation}" }

                positionedRenderingElements.addAll(extraBarLinesElement.toRenderingElement())
            }
        }

//        if(context.debug) {
//            addDebugBox(determineViewBox(positionedRenderingElements)).let { positionedRenderingElements.addAll(it.toRenderingElement()) }
//        }
    }

    private fun setupAccidental(accidental: Accidental): PositionedRenderingElementParent {
        return getAccidentalGlyph(accidental).let { accidentalGlyph ->
            PositionedRenderingElement.create(
                accidentalGlyph.boundingBox,
                id,
                (translation ?: Translation(0, 0)).let {
                    Translation(it.xShift - 30, it.yShift)
                },
                accidental.name,
                true
            )
        }
    }

    fun getStem(): PositionedRenderingElement {
        val stem = addStem(getNoteHeadGlyph(duration).boundingBox, note.stem != Stem.DOWN)

        return TranslatedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements), context.getAndIncrementStemCounter(),
            null, translation ?: Translation(0, 0)
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

    override fun toString(): String {
        return "NoteElement(note=$note, translation=$translation)"
    }

}
