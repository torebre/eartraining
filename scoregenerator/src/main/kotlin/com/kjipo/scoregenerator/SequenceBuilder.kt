package com.kjipo.scoregenerator


import com.kjipo.score.RenderingSequence

class SequenceBuilder private constructor() {
    private val sequence: Sequence


    init {
        sequence = Sequence()
    }

    fun setClefType(clefType: Clef): SequenceBuilder {
        sequence.clef = clefType
        return this
    }

    fun setTimeSignatureNominator(timeSignatureNominator: Int): SequenceBuilder {
        sequence.timeSignatureNominator = timeSignatureNominator
        return this
    }

    fun setTimeSignatureDenominator(timeSignatureDenominator: Int): SequenceBuilder {
        sequence.timeSignatureDenominator = timeSignatureDenominator
        return this
    }

    fun setDurationOfBar(durationOfBar: Int): SequenceBuilder {
        sequence.durationOfBar = durationOfBar
        return this
    }

    fun setTempoInMillisecondsPerQuarterNote(
            tempoInMillisecondsPerQuarterNote: Int): SequenceBuilder {
        sequence.tempoInMillisecondsPerQuarterNote = tempoInMillisecondsPerQuarterNote
        return this
    }

    fun addPitch(pitch: Pitch): SequenceBuilder {
        sequence.addPitch(pitch)
        return this
    }

    fun addRenderingSequence(renderingSequence: RenderingSequence): SequenceBuilder {
        sequence.renderingSequence = renderingSequence
        return this
    }

    fun build(): Sequence {
        return Sequence(sequence)
    }

    companion object {

        fun createSequence(): SequenceBuilder {
            return SequenceBuilder()
        }
    }

}
