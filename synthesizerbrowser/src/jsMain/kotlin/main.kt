import com.kjipo.midi.SynthesizerScript
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import mu.KotlinLogging
import org.w3c.dom.events.KeyboardEvent

object WebScoreApp {
    private val synthesizer = SynthesizerScript()
    private val webscoreShow = WebscoreShow(synthesizer)

    private val logger = KotlinLogging.logger {}

    private var currentMode = WebscoreInputMode.MOVE

    private val webscoreListener: WebscoreListener = object : WebscoreListener {

        override fun noteInputMode(webscoreInputMode: WebscoreInputMode) {
            setCurrentMode(webscoreInputMode)
        }

        override fun currentStep(currentStep: NoteInput.NoteInputStep) {
            setTextForChildNode(
                "#currentStep", currentStep.name
            )
            setAllowedInputText(currentStep)
        }

    }


    private fun setCurrentMode(webscoreInputMode: WebscoreInputMode) {
        if (currentMode != webscoreInputMode) {
            var inputModeText = ""
            when (webscoreInputMode) {
                WebscoreInputMode.NOTE -> {
                    inputModeText = "Note input"
                }

                WebscoreInputMode.REST -> {
                    inputModeText = "Rest input"
                }

                WebscoreInputMode.MOVE -> {
                    inputModeText = "Move"
                }

                WebscoreInputMode.EDIT -> {
                    inputModeText = "Edit element"
                }
            }
            setTextForChildNode("#inputMode", inputModeText)
            setAllowedInputText(webscoreInputMode)
        }
        currentMode = webscoreInputMode
    }


    private fun setAllowedInputText(webscoreInputMode: WebscoreInputMode) {
        when (webscoreInputMode) {
            WebscoreInputMode.MOVE -> {
                "H for left. L for right"
            }

            WebscoreInputMode.EDIT -> {
                "J for down. K for up"
            }

            WebscoreInputMode.NOTE, WebscoreInputMode.REST -> {
                null
            }
        }?.let { allowedInputText ->
            setTextForAllowedInputNode(allowedInputText)
        }
    }

    private fun setAllowedInputText(currentStep: NoteInput.NoteInputStep) {
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

            NoteInput.NoteInputStep.None -> {
                "None"
            }

        }
        setTextForAllowedInputNode(helperText)
    }

    private fun setTextForAllowedInputNode(text: String) {
        setTextForChildNode("#allowedInput", text)
    }


    private fun handleKeyEvent(event: KeyboardEvent) {
        when (event.code) {
            "KeyP" -> {
                playInputSequence()
            }

            "KeyT" -> {
                playTargetSequence()
            }
        }

    }


    fun showWebscore() {
//        val synthesizer = SynthesizerScript()
//        val webscoreShow = WebscoreShow(synthesizer)
        webscoreShow.createSequence(webscoreListener)
//        webscoreShow.createInputScore()
//        webscoreShow.inputScore?.addListener(webscoreListener)

        document.addEventListener("keydown", { event ->
            val keyboardEvent = event as KeyboardEvent
            handleKeyEvent(keyboardEvent)
        })

        document.querySelector("#btnNew")!!.addEventListener("click", {
            webscoreShow.createSequence(webscoreListener)
            // Starts in move-mode when a new sequence is created
            setCurrentMode(WebscoreInputMode.MOVE)
        })

        document.querySelector("#playTarget")!!.addEventListener("click", {
            webscoreShow.playTargetSequence()
        })

        document.querySelector("#playInput")!!.addEventListener("click", {
            webscoreShow.playInputSequence()
        })

        document.querySelector("#btnSubmit")?.addEventListener("click", {
            webscoreShow.submit()
        })

    }

    private fun playTargetSequence() {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playTargetSequence()
        }
    }

    private fun playInputSequence() {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playInputSequence()
        }
    }

    private fun setTextForChildNode(idSelector: String, textContent: String) {
        document.querySelector(idSelector)?.let { element ->
            element.firstChild?.let {
                it.textContent = textContent
            }
        }
    }

}


fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    WebScoreApp.showWebscore()
}
