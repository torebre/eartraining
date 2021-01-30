import com.kjipo.handler.ScoreHandlerUtilities
import com.kjipo.score.NoteType

class SynthesizerScript: MidiPlayerInterface {
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
        val (noteType, octave) = ScoreHandlerUtilities.pitchToNoteAndOctave(pitch)

        val note = when(noteType) {
            NoteType.A -> "A"
            NoteType.A_SHARP -> "A#"
            NoteType.H -> "B"
            NoteType.C -> "C"
            NoteType.C_SHARP -> "C#"
            NoteType.D -> "D"
            NoteType.D_SHARP -> "D#"
            NoteType.E -> "E"
            NoteType.F -> "F"
            NoteType.F_SHARP -> "F#"
            NoteType.G -> "G"
            NoteType.G_SHARP -> "G#"
        }

        return note + octave
    }

}