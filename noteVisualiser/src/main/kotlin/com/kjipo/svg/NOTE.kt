package com.kjipo.svg

class NOTE(consumer: ElementConsumer<*>) : ScoreElement(consumer) {
//    var pitch: Int = 0
    var note: NoteType = NoteType.C
    var modifier: NoteModifier = NoteModifier.NONE
    var octave = 5
    var duration = 1
    var beamGroup: Int = 0

}