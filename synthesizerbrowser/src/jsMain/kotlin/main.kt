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
import mu.KotlinLogging

object WebScoreApp {

    private val logger = KotlinLogging.logger {}

    private val webscoreListener: WebscoreListener = object : WebscoreListener {

        override fun noteInputMode(noteInputMode: Boolean) {
            setTextForChildNode(
                "#inputMode", if (noteInputMode) {
                    "Note input on"
                } else {
                    "Note input off"
                }
            )
        }

        override fun noteInputNotRest(noteInput: Boolean) {
            setTextForChildNode(
                "#noteOrRestInput", if (noteInput) {
                    "Note input"
                } else {
                    "Rest input"
                }
            )
        }

        override fun currentStep(currentStep: NoteInput.NoteInputStep?) {
            setTextForChildNode(
                "#currentStep", currentStep?.name ?: "None"
            )
            setAllowedInputText(currentStep)
        }

        private fun setAllowedInputText(currentStep: NoteInput.NoteInputStep?) {
            logger.debug {
                "Current step: $currentStep"
            }

            val helperText = when (currentStep) {
                NoteInput.NoteInputStep.Duration -> {
                    "1, 2, 3, 4"
                }
                NoteInput.NoteInputStep.Note -> {
                    "a, h, c, d, e, f, g"
                }
                NoteInput.NoteInputStep.Modifier -> {
                    "#"
                }
                NoteInput.NoteInputStep.Octave -> {
                    "1 to 12"
                }
                null -> {
                    ""
                }
            }
            setTextForChildNode("#allowedInput", helperText)
        }
    }


    fun showWebscore() {
        val synthesizer = SynthesizerScript()
        val webscoreShow = WebscoreShow(synthesizer)
        webscoreShow.createSequence()
        webscoreShow.createInputScore()
        webscoreShow.inputScore?.addListener(webscoreListener)

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

    private fun setTextForChildNode(idSelector: String, textContent: String) {
        document.querySelector(idSelector)?.let { element ->
            element.firstChild?.let {
                it.textContent = textContent
            }
        }
    }

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
    WebScore(ScoreHandlerJavaScript(splitScoreHandler), "scaleTest", false)
}


fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    WebScoreApp.showWebscore()
//    showScaleTest()
}
