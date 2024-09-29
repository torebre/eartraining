package com.kjipo.midi


class SynthesizerScript : MidiPlayerInterface {
    private val sampler: Tone.Sampler

    init {
        val samples: dynamic = Any()
        samples.C1 = "UR1_C1_f_RR1.wav"
        samples.C2 = "UR1_C2_f_RR1.wav"
        samples.C3 = "UR1_C3_f_RR1.wav"
        samples.C4 = "UR1_C4_f_RR1.wav"
        samples.C5 = "UR1_C5_f_RR1.wav"
        samples.C6 = "UR1_C6_f_RR1.wav"
        samples.C7 = "UR1_C7_f_RR1.wav"

        val parameters: dynamic = Any()
        parameters.release = 1
        parameters.baseUrl = "samples/"

        sampler = Tone.Sampler(samples, parameters)
        sampler.toMaster()
    }


    override fun noteOn(pitch: Int) {
        sampler.triggerAttack(pitchToNote(pitch))
    }

    override fun noteOff(pitch: Int) {
        sampler.triggerRelease(pitchToNote(pitch))
    }

    override fun releaseAll() {
        sampler.releaseAll()
    }

    override fun start() {
        Tone.Transport.start()
    }

    override fun stop() {
        Tone.Transport.stop()
    }


    private fun pitchToNote(pitch: Int): String {
        val remainder = pitch.rem(12)
        val noteType = when (remainder) {
            9 -> "A"
            10 -> "A#"
            11 -> "B"
            0 -> "C"
            1 -> "C#"
            2 -> "D"
            3 -> "D#"
            4 -> "E"
            5 -> "F"
            6 -> "F#"
            7 -> "G"
            8 -> "G#"
            else -> throw IllegalArgumentException("Unhandled pitch: $pitch")
        }

        return noteType + pitch.minus(remainder).div(12)
    }

}