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
            val midiScript = PolyphonicSequenceScript(midiEventSequence, synthesizer)
            midiScript.play()
        }
    })
}

fun main() {
    playGeneratedSequence()
}
