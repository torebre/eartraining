package com.kjipo.svg

class NOTE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var pitch: Int = 0
    var duration = 1
    var beamGroup: Int = 0

}