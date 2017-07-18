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


fun createScore() = ScoreBuilder()
