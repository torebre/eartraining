package com.kjipo.score

class NOTE(consumer: ScoreBuilderInterface<*>) : ScoreElement(consumer) {
    var note = NoteType.C
    var modifier = NoteModifier.NONE
    var octave = 5
    var duration = Duration.QUARTER
    var beamGroup: Int = 0

}