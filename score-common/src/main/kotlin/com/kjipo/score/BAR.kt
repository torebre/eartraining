package com.kjipo.score


class BAR(consumer: ScoreBuilderInterface<*>) : ScoreElement(consumer) {
    val barData = BarData(consumer.debug)

    fun note(init: NOTE.() -> Unit) = doInit(NOTE(consumer), init)

    fun rest(init: REST.() -> Unit) = doInit(REST(consumer), init)

    fun build(barXoffset: Int = 0, barYoffset: Int = 0): List<RenderingSequence> = listOf(barData.build(barXoffset, barYoffset))

}
