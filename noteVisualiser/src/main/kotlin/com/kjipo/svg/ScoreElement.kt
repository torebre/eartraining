package com.kjipo.svg

import com.kjipo.font.GlyphFactory
import com.kjipo.font.NoteType


interface ElementConsumer<out T> {
    fun onNoteAdded(note: NOTE)
    fun build(): T
}

open class ScoreElement(val consumer: ElementConsumer<*>) {

    private val children = mutableListOf<ScoreElement>()


    protected fun <T : ScoreElement> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)

        when {
            child is NOTE -> consumer.onNoteAdded(child)
        }
    }


}

fun <T : ScoreElement, R> T.finalize(consumer: ElementConsumer<R>): R {
    return consumer.build()
}

class SCORE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    fun bar(init: BAR.() -> Unit) {
        doInit(BAR(consumer), init)
    }
}


class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var clef: Clef = Clef.G
    var key: Key = Key.C

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)
}

class NOTE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var pitch: Int = 0
    var duration = 1

}


class ScoreBuilder : ElementConsumer<RenderingSequence> {
    private val renderingElements = mutableListOf<RenderingElementImpl>()
    private val internalLocation = mutableListOf<Point>()


    var nominator = 4
    var denominator = 4
    var ticksPerQuarterNote = 24

    var measureWidth = 1000

    private var counter = 0


    override fun onNoteAdded(note: NOTE) {

        println("Test20")

        renderingElements.add(translateToRenderingElement(note))

        // TODO
        internalLocation.add(Point(counter, note.pitch))

        counter += note.duration

    }


    private fun layout(): List<Point> {
        // TODO Figure out better solution
        val pixelsPerTick = measureWidth / ticksInMeasure()

        return internalLocation.map { Point(it.x * pixelsPerTick, it.y) }

    }


    private fun ticksInMeasure(): Int {
        return nominator * denominatorInTicks()


    }

    private fun denominatorInTicks(): Int {
        // TODO Is this correct?
        return nominator.div(4).times(ticksPerQuarterNote)
    }


    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build(): RenderingSequence {
        val points = layout()

        return RenderingSequence(renderingElements, points)
    }

    fun translateToRenderingElement(note: NOTE): RenderingElementImpl {

        // TODO

        val glyph = when {
            note.duration == 24 -> GlyphFactory.getGlyph(NoteType.QUARTER_NOTE)
            note.duration == 48 -> GlyphFactory.getGlyph(NoteType.HALF_NOTE)
            else -> GlyphFactory.blankGlyph
        }

        return RenderingElementImpl(listOf(glyph))
    }


}

fun createScore() = ScoreBuilder()



