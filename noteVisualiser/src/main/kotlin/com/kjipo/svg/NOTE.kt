package com.kjipo.svg

class NOTE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
    var note = NoteType.C
    var modifier = NoteModifier.NONE
    var octave = 5
    var duration = Duration.QUARTER
    var beamGroup: Int = 0

}