package com.kjipo.score


interface ScoreBuilderInterface<out T> {

    val debug: Boolean

    fun onBarAdded(bar: BAR)
    fun onNoteAdded(note: NOTE): String
    fun onRestAdded(rest: REST)
    fun build(): T
}

open class ScoreElement(val consumer: ScoreBuilderInterface<*>) {

    private val children = mutableListOf<ScoreElement>()

    protected fun <T : ScoreElement> doInit(child: T, init: T.() -> Unit) {
        child.init()
        children.add(child)

        when {
            child is NOTE -> consumer.onNoteAdded(child)
            child is REST -> consumer.onRestAdded(child)
            child is BAR -> consumer.onBarAdded(child)
        }
    }

}

fun <T : ScoreElement, R> T.finalize(consumer: ScoreBuilderInterface<R>): R {
    return consumer.build()
}


fun createScore() = ScoreBuilderImpl()
