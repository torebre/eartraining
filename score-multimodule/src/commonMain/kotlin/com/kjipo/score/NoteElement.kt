package com.kjipo.score

import com.kjipo.score.BarData.Companion.stemCounter
import com.kjipo.svg.*
import mu.KotlinLogging


class NoteElement(
    var note: GClefNoteLine,
    var octave: Int,
    override var duration: Duration,
    val context: Context,
    override val id: String = context.getAndIncrementIdCounter(),
    override val properties: Map<String, String> = mapOf()
) : ScoreRenderingElement(), TemporalElement, HighlightableElement {
    var accidental: Accidental? = null
    private var stem = Stem.NONE
    val positionedRenderingElements = mutableListOf<PositionedRenderingElement>()

    private val highlightElements = mutableSetOf<String>()

    private val logger = KotlinLogging.logger {}


    override fun toRenderingElement(): List<PositionedRenderingElement> {
        return positionedRenderingElements
    }

    fun layoutNoteHeads() {
        accidental?.run {
            positionedRenderingElements.add(setupAccidental(this))
        }

        val glyphData = getNoteHeadGlyph(duration)
        val positionedRenderingElement = PositionedRenderingElement.create(
            listOf(PathInterfaceImpl(glyphData.pathElements, 1)),
            glyphData.boundingBox,
            id,
            translation ?: Translation(0, 0),
            duration.name
        )
        positionedRenderingElements.add(positionedRenderingElement)
        highlightElements.add(positionedRenderingElement.id)

        addExtraBarLinesForGClef(
            note, octave,
            translation?.xShift ?: 0,
            translation?.yShift ?: 0,
            glyphData.boundingBox.xMin.toInt(),
            glyphData.boundingBox.xMax.toInt(),
            context.getAndIncrementExtraBarLinesCounter()
        )?.let { extraBarLinesElement ->
//            extraBarLinesElement.translation = Translation(translation?.xShift ?: 0, 0)

            logger.debug { "Extra bar lines element translation: ${extraBarLinesElement.translation}" }

            positionedRenderingElements.addAll(extraBarLinesElement.toRenderingElement())
        }
    }


    private fun setupAccidental(accidental: Accidental): PositionedRenderingElement {
        val accidentalGlyph = getAccidentalGlyph(accidental)
        return PositionedRenderingElement.create(
            listOf(PathInterfaceImpl(accidentalGlyph.pathElements, 1)), accidentalGlyph.boundingBox, id,
            translation ?: Translation(0, 0),
            accidental.name
        ).apply {
            translation = translation.let {
                Translation(it.xShift - 30, it.yShift)
            }
        }
    }

    fun getStem(): PositionedRenderingElement {
        val stem = addStem(getNoteHeadGlyph(duration).boundingBox, stem != Stem.DOWN)

        return TranslatedRenderingElement(
            listOf(stem),
            findBoundingBox(stem.pathElements),
            "stem-${BarData.barNumber++}-${stemCounter++}", null, translation ?: Translation(0, 0)
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

        accidental?.let {
            glyphsUsed.put(it.name, getAccidentalGlyph(it))
        }

        if (duration == Duration.EIGHT) {
            getFlagGlyph(Duration.EIGHT, stem == Stem.UP).let {
                glyphsUsed[it.name] = it
            }
        }

        glyphsUsed[duration.name] = getNoteHeadGlyph(duration)

        return glyphsUsed
    }

    fun addStem(stem: Stem) {
        this.stem = stem
        positionedRenderingElements.add(getStem())
    }

    override fun getIdsOfHighlightElements() = highlightElements

    override fun toString(): String {
        return "NoteElement(note=$note, octave=$octave, duration=$duration, translation=$translation)"
    }

}
