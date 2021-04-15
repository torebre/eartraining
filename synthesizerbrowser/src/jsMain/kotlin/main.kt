import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.scoregenerator.ELEMENT_ID
import com.kjipo.scoregenerator.ReducedScore
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

    document.querySelector("btnSubmit")?.addEventListener("click", {
        webscoreShow.submit()
    })
}

//fun showScaleTest() {
//    val synthesizer = SynthesizerScript()
//    val noteSequence =
//        SimpleNoteSequence(NoteType.values().leftShift(3).map { NoteSequenceElement.NoteElement(it, 5, Duration.QUARTER) }.toList())
//    val sequenceGenerator = SequenceGenerator()
//
//    sequenceGenerator.loadSimpleNoteSequence(noteSequence)
//    val webScore = WebScore(ScoreHandlerJavaScript(sequenceGenerator), "scaleTest", false)
//
//    document.querySelector("#playScaleTest")!!.addEventListener("click", {
//        GlobalScope.launch(Dispatchers.Default) {
//            playSequenceInternal(sequenceGenerator.getActionSequenceScript(), webScore, synthesizer)
//        }
//    })
//
//}

fun showScaleTest() {
    var idCounter = 0
    val noteSequence: MutableList<NoteSequenceElement> =
        NoteType.values().leftShift(3)
            .map {
                val elementId = (++idCounter).toString()
                NoteSequenceElement.NoteElement(elementId, it, 5, Duration.QUARTER, mapOf(Pair(ELEMENT_ID, elementId)))
            }
            .toMutableList()

    val splitScoreHandler = ReducedScore().also { it.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence)) }
    val webScore = WebScore(ScoreHandlerJavaScript(splitScoreHandler), "scaleTest", false)
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
//    showScaleTest()
}
