package com.kjipo.svg

class SCORE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    fun bar(init: BAR.() -> Unit) {
        doInit(BAR(consumer), init)
    }
}