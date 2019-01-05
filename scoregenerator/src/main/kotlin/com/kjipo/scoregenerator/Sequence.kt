package com.kjipo.scoregenerator


import com.kjipo.score.RenderingSequence
import com.kjipo.score.ViewBox

class Sequence {
    var clef: Clef? = null
    var timeSignatureNominator: Int = 0
    var timeSignatureDenominator: Int = 0
    var durationOfBar: Int = 0
    var tempoInMillisecondsPerQuarterNote: Int = 0

    var pitchSequence = mutableListOf<Pitch>()
    var renderingSequence: RenderingSequence = RenderingSequence(emptyList(), ViewBox(0, 0, 0, 0), emptyMap())


    constructor()

    constructor(sequence: Sequence) : this(sequence.clef, sequence.timeSignatureNominator,
            sequence.timeSignatureDenominator,
            sequence.renderingSequence,
            sequence.durationOfBar,
            sequence.tempoInMillisecondsPerQuarterNote,
            sequence.pitchSequence)


    constructor(clef: Clef?, timeSignatureNominator: Int, timeSignatureDenominator: Int,
                renderingSequence: RenderingSequence?,
                durationOfBar: Int,
                tempoInMillisecondsPerQuarterNote: Int,
                pitchSequence: MutableList<Pitch>) {
        this.clef = clef
        this.timeSignatureNominator = timeSignatureNominator
        this.timeSignatureDenominator = timeSignatureDenominator
        this.renderingSequence = renderingSequence?: RenderingSequence(emptyList(), ViewBox(0, 0, 0, 0), emptyMap())
        this.durationOfBar = durationOfBar
        this.tempoInMillisecondsPerQuarterNote = tempoInMillisecondsPerQuarterNote
        this.pitchSequence = pitchSequence
    }

    fun addPitch(pitch: Pitch) {
        pitchSequence.add(pitch)
    }
}
