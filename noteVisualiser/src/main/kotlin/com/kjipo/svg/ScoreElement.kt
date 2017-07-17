package com.kjipo.svg


interface ElementConsumer<out T> {
    fun onBarAdded(bar: BAR)
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
            child is BAR -> consumer.onBarAdded(child)
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
    var clef: Clef? = null
    var key: Key = Key.C

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)
}

class NOTE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var pitch: Int = 0
    var duration = 1

}


class ScoreBuilder : ElementConsumer<RenderingSequence> {
    private val scoreRenderingElements = mutableListOf<ScoreRenderingElement>()

//    private val renderingElements = mutableListOf<RenderingElementImpl>()
//    private val internalLocation = mutableListOf<Point>()


    var nominator = 4
    var denominator = 4
    var ticksPerQuarterNote = 24

    var measureWidth = 1000

    private var counter = 0


    override fun onBarAdded(bar: BAR) {
        // TODO Figure out best way to set the value
        val clef = bar.clef
        if (clef != null) {
            // TODO Set correct position
            scoreRenderingElements.add(ClefElement(clef, counter, 0))
        }
    }

    override fun onNoteAdded(note: NOTE) {
        // TODO Set proper location
        val scoreRenderingElement = NoteElement(counter, note.pitch)
        scoreRenderingElement.notes.add(note)

        scoreRenderingElements.add(scoreRenderingElement)
        counter += note.duration
    }


    private fun layout(): List<Point> {
        // TODO Figure out better solution
        val pixelsPerTick = measureWidth / ticksInMeasure()

        return scoreRenderingElements.map { Point(it.xPosition * pixelsPerTick, it.yPosition) }
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

        return RenderingSequence(scoreRenderingElements.map { it.toRenderingElement() }, points)
    }


}

fun createScore() = ScoreBuilder()
