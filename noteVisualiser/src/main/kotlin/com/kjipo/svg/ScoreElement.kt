package com.kjipo.svg


interface ElementConsumer<out T> {
    fun onNoteAdded(note : NOTE)
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


class SCORE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    fun bar(init: BAR.() -> Unit) = doInit(BAR(consumer), init)
}


class BAR(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var key: Key? = null

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)
}

class NOTE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var pitch: Int = 0
    var duration = 1

}


class ScoreBuilder : ElementConsumer<RenderingSequence> {
    override fun onNoteAdded(note: NOTE) {

        println("Test20")

    }

    private val renderingElements = mutableListOf<RenderingElement>()
    private val points = mutableListOf<Point>()

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init)

    override fun build() : RenderingSequence {
        return RenderingSequence(renderingElements, points)
    }

}

fun createScore() = ScoreBuilder()



