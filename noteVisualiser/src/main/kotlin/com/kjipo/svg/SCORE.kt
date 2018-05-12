package com.kjipo.svg

class SCORE(consumer: ScoreBuilderInterface<*>) : ScoreElement(consumer) {
    fun bar(init: BAR.() -> Unit) {
        doInit(BAR(consumer), init)
    }
}