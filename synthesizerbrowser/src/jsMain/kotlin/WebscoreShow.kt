import com.kjipo.score.NoteSequenceElement
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.ReducedScore
import com.kjipo.submithandling.SubmitHandler
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

    fun createSequence() {
        targetSequenceGenerator = ReducedScore()
        polyphonicNoteSequenceGenerator.createSequence().apply {
            targetSequenceGenerator.loadSimpleNoteSequence(this)
            submitHandler.setupExercise(elements)
        }

        webScore = WebScore(ScoreHandlerJavaScript(targetSequenceGenerator), "targetScore", false)
    }

    fun createInputScore() {
        inputSequenceGenerator = ReducedScore()
        inputScore = WebScore(ScoreHandlerJavaScript(inputSequenceGenerator), "inputScore", true, "debug")
    }

    fun submit() {
        val attempt = inputSequenceGenerator.noteSequence
        submitHandler.getCurrentExercise()?.submit(attempt)
    }

    fun submit(attempt: List<NoteSequenceElement>) {
        submitHandler.getCurrentExercise()?.submit(attempt)
    }

    suspend fun playSequence() {
        playSequenceInternal(targetSequenceGenerator.getActionSequenceScript(), webScore, midiInterface)
    }

    suspend fun playInputSequence() {
        playSequenceInternal(inputSequenceGenerator.getActionSequenceScript(), inputScore, midiInterface)
    }

}

