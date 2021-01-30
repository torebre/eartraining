import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun playSingleNote() {
    val synthesizer = SynthesizerScript()
    var notePlaying = false

    document.querySelector("button")!!.addEventListener("click", {
        if (notePlaying) {
            synthesizer.noteOff(60)
        } else {
            synthesizer.noteOn(60)
        }

        notePlaying = !notePlaying
    })
}

fun playGeneratedSequence() {
    val synthesizer = SynthesizerScript()
    val sequenceGenerator = PolyphonicNoteSequenceGenerator()

    document.querySelector("button")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            val noteSequence = sequenceGenerator.createSequence()
            val midiEventSequence = PolyphonicNoteSequenceGenerator.transformToSimplePitchEventSequence(noteSequence)
            val midiScript = PolyphonicSequenceScript(midiEventSequence.pitches, synthesizer)
            midiScript.play()
        }
    })
}

fun showWebscore() {
    val synthesizer = SynthesizerScript()
    val webscoreShow = WebscoreShow(synthesizer)
    webscoreShow.createSequence()

    document.querySelector("button")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playSequence()
        }
    })

}

fun main() {
//    playGeneratedSequence()

    showWebscore()

}
