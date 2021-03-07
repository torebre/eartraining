import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.scoregenerator.SequenceGenerator
import com.kjipo.scoregenerator.SimpleNoteSequence
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel


fun showWebscore() {
    val synthesizer = SynthesizerScript()
    val webscoreShow = WebscoreShow(synthesizer)
    webscoreShow.createSequence()
    webscoreShow.createInputScore()

    document.querySelector("#playTarget")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playSequence()
        }
    })

    document.querySelector("#playInput")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playInputSequence()
        }
    })
}

fun showScaleTest() {
    val synthesizer = SynthesizerScript()
    val noteSequence =
        SimpleNoteSequence(NoteType.values().leftShift(3).map { NoteSequenceElement.NoteElement(it, 5, Duration.QUARTER) }.toList())
    val sequenceGenerator = SequenceGenerator()

    sequenceGenerator.loadSimpleNoteSequence(noteSequence)
    val webScore = WebScore(ScoreHandlerJavaScript(sequenceGenerator), "scaleTest", false)

    document.querySelector("#playScaleTest")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            playSequenceInternal(sequenceGenerator.getActionSequenceScript(), webScore, synthesizer)
        }
    })

}

fun <T> Array<T>.leftShift(positionsToShift: Int): Array<T> {
    val newList = this.copyOf()
    var shift = positionsToShift
    if (shift > size) shift %= size
    forEachIndexed { index, value ->
        val newIndex = (index + (size - shift)) % size
        newList[newIndex] = value
    }
    return newList
}

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    showWebscore()
    showScaleTest()
}
