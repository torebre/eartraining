import com.kjipo.score.NoteSequenceElement
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.ReducedScore
import com.kjipo.submithandling.SubmitHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging

/**
 * Shows two scores. One with the target sequence and one with the
 * user input sequence.
 */
class WebscoreShow(private val midiInterface: MidiPlayerInterface) {

    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
    private var targetSequenceGenerator = ReducedScore()
    private var inputSequenceGenerator = ReducedScore()
    private var webScore: WebScore? = null
    private var inputScore: WebScore? = null

    private val submitHandler = SubmitHandler()

    private val logger = KotlinLogging.logger {}


    fun createSequence(webscoreListener: WebscoreListener) {
        targetSequenceGenerator = ReducedScore()
        polyphonicNoteSequenceGenerator.createSequence().apply {
            targetSequenceGenerator.loadSimpleNoteSequence(this)
            submitHandler.setupExercise(elements)
        }
        createInputScore().addListener(webscoreListener)

        webScore = WebScore(ScoreHandlerJavaScript(targetSequenceGenerator), "targetScore", false)
    }

    private fun createInputScore(): WebScore {
        inputSequenceGenerator = ReducedScore()
        return WebScore(
            ScoreHandlerJavaScript(inputSequenceGenerator),
            "inputScore",
            true
        ).also { webScore -> inputScore = webScore }
    }

    fun submit() {
        submitHandler.getCurrentExercise()?.submit(inputSequenceGenerator.getCurrentNoteSequence())
    }

    fun submit(attempt: List<NoteSequenceElement>) {
        submitHandler.getCurrentExercise()?.submit(attempt)
    }

    private suspend fun playTargetSequenceInternal() {
        playTargetSequenceInternal(targetSequenceGenerator.getActionSequenceScript(), webScore, midiInterface)
    }

    private suspend fun playInputSequenceInternal() {
        playTargetSequenceInternal(inputSequenceGenerator.getActionSequenceScript(), inputScore, midiInterface)
    }

    fun playInputSequence() {
        GlobalScope.launch(Dispatchers.Default) {
            playInputSequenceInternal()
        }
    }

    fun playTargetSequence() {
        GlobalScope.launch(Dispatchers.Default) {
            playTargetSequenceInternal()
        }
    }

}

