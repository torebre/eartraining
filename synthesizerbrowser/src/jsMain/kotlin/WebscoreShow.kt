import com.kjipo.handler.ScoreHandlerListener
import com.kjipo.handler.ScoreHandlerWrapper
import com.kjipo.score.NoteSequenceElement
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.SequenceGenerator
import com.kjipo.submithandling.SubmitHandler
import mu.KotlinLogging

class WebscoreShow(private val midiInterface: MidiPlayerInterface) {

    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
    private var targetSequenceGenerator = SequenceGenerator()
    private var inputSequenceGenerator = SequenceGenerator()
    private var webScore: WebScore? = null
    private var inputScore: WebScore? = null

    private val submitHandler = SubmitHandler()

    private val logger = KotlinLogging.logger {}

    fun createSequence() {
        targetSequenceGenerator = SequenceGenerator()
        polyphonicNoteSequenceGenerator.createSequence().apply {
            targetSequenceGenerator.loadSimpleNoteSequence(this)
            submitHandler.setupExercise(elements)
        }

        webScore = WebScore(ScoreHandlerJavaScript(targetSequenceGenerator), "targetScore", false)
    }

    fun createInputScore() {
        inputSequenceGenerator = SequenceGenerator()
        val scoreHandlerWrapper = ScoreHandlerWrapper(inputSequenceGenerator)

        scoreHandlerWrapper.addListener(object : ScoreHandlerListener {
            override fun pitchSequenceChanged() {
                logger.debug { "Sequence changed" }
            }
        })

        inputScore = WebScore(ScoreHandlerJavaScript(scoreHandlerWrapper), "inputScore")
    }

    fun submit() {

        // TODO

//        inputSequenceGenerator.scoreHandler
//        submitHandler.getCurrentExercise()?.submit(attempt)
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

