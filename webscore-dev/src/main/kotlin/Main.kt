import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreHandlerWrapper
import com.kjipo.score.Duration
import com.kjipo.scoregenerator.SequenceGenerator
import com.kjipo.scoregenerator.SimpleSequenceGenerator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlin.browser.document


private fun setupTestScore2() {
    val scoreHandler = ScoreHandler()

    val note1 = scoreHandler.insertNote(Duration.EIGHT)
    val note2 = scoreHandler.insertNote(Duration.EIGHT)
    val note3 = scoreHandler.insertNote(Duration.QUARTER)
    val note4 = scoreHandler.insertNote(Duration.QUARTER)

//    scoreHandler.addBeams(listOf(note2, note3))

    WebScore(ScoreHandlerJavaScript(scoreHandler))
}


private fun setupEmptyBar() {
    val scoreHandler = ScoreHandler()

    scoreHandler.insertRest(Duration.QUARTER)
    scoreHandler.insertRest(Duration.QUARTER)
    scoreHandler.insertRest(Duration.QUARTER)
    scoreHandler.insertRest(Duration.QUARTER)

    WebScore(ScoreHandlerJavaScript(scoreHandler))
}

private fun playNote() {

    val samples: dynamic = Any()
//        samples.C1 = "samples/UR1_C1_f_RR1.wav"
//    samples.C1 = "chord.mp3"
    samples.C1 = "UR1_C1_f_RR1.wav"
    samples.C2 = "UR1_C2_f_RR1.wav"
    samples.C3 = "UR1_C3_f_RR1.wav"
    samples.C4 = "UR1_C4_f_RR1.wav"
    samples.C5 = "UR1_C5_f_RR1.wav"
    samples.C6 = "UR1_C6_f_RR1.wav"
    samples.C7 = "UR1_C7_f_RR1.wav"
//    samples.G2 = "UR1_G2_f_RR1.wav"
//    samples.G3 = "UR1_G3_f_RR1.wav"
//    samples.G4 = "UR1_G4_f_RR1.wav"
//    samples.G5 = "UR1_G5_f_RR1.wav"
//    samples.G6 = "UR1_G6_f_RR1.wav"
//    samples.G7 = "UR1_G7_f_RR1.wav"

    val parameters: dynamic = Any()
    parameters.release = 1
    parameters.baseUrl = "samples/"

    var sampler = Tone.Sampler(samples, parameters)
    sampler.toMaster()

    Tone.Transport.start()
}


fun main() {
    var simpleNoteSequence = SimpleSequenceGenerator.createSequence()
    val sequenceGenerator = SequenceGenerator()
    sequenceGenerator.loadSimpleNoteSequence(simpleNoteSequence)

//    val scoreHandlerWrapper = ScoreHandlerWrapper(sequenceGenerator)
    val webScore = WebScore(ScoreHandlerJavaScript(sequenceGenerator))


    val synthesizerScript = SynthesizerScript()

    var midiScript = MidiScript(sequenceGenerator.pitchSequence, synthesizerScript)
    var currentPlayJob: Job? = null

    document.querySelector("#playButton")?.addEventListener("click", {
        val previousPlayJob = currentPlayJob

        currentPlayJob = GlobalScope.launch {
            previousPlayJob?.cancelAndJoin()

            console.log("Playing sequence: ${sequenceGenerator.pitchSequence}")

            midiScript = MidiScript(sequenceGenerator.pitchSequence, synthesizerScript)
            midiScript.play()
        }

    })

    document.querySelector("#generateSequence")?.addEventListener("click", {
        val sequence = SimpleSequenceGenerator.createSequence()

        midiScript = MidiScript(sequence.transformToPitchSequence(), synthesizerScript)
        sequenceGenerator.loadSimpleNoteSequence(sequence)

        webScore.reload()
    })

}
