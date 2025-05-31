import com.kjipo.attemptprocessor.PitchData
import com.kjipo.midi.SynthesizerScript
import com.kjipo.midi.playTargetSequenceInternal2
import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.SimpleNoteSequence
import com.kjipo.scoregenerator.actionScript
import com.kjipo.scoregenerator.computePitchSequence
import graph.PitchDataWithTime
import graph.PitchGraph
import graph.PitchGraphModel
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import kotlin.math.pow


object WebPitchApp {

    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()

    private val synthesizer = SynthesizerScript()
    private var currentSequence = polyphonicNoteSequenceGenerator.createSequence(false)
    private var actionSequence: MutableList<Action>
    private var isRecording = false

    private val rateInput: RateInput = RateInput()

    private val pitchGraphModel = PitchGraphModel()

    private val logger = KotlinLogging.logger {}

    init {
        val (pitches, actionSequence) = computePitchSequence(currentSequence.elements)
        this.actionSequence = actionSequence
        this.rateInput.setCurrentTarget(pitches)
    }


    fun start() {
        setupPlayButton()
        setupGenerateSequenceButton()
        setupShowTargetSequenceButton()

        // TODO Why does it not work when PitchDetection is a class?
        var pitchDetection: PitchDetection? = null

//    val pitchGraphModel = RandomPitchGraphModel()
        val pitchGraph = PitchGraph("pitchGraph", pitchGraphModel)

        document.querySelector("#btnToggleRecording")?.let { recordingButton ->
            logger.info { "Adding event listener" }

            recordingButton.addEventListener("click", {
                if (!isRecording) {
                    if (pitchDetection == null) {
                        pitchDetection = PitchDetection.also {
                            it.addPitchDetectionListener(object : PitchDetectionListener {
                                override fun pitchData(pitchData: PitchData) {
                                    logger.info { "Pitch: ${pitchData.pitch}. Certainty: ${pitchData.certainty}" }
                                    setTextForChildNode("#pitchLabel", pitchData.pitch.toString())
                                    setTextForChildNode("#certaintyLabel", pitchData.certainty.toString())

                                    rateInput.addPitchData(pitchData)
                                }
                            })
                        }
                        pitchDetection = PitchDetection
                    }

                    pitchDetection?.let {
                        rateInput.startNewInput()
                        it.startRecording()
                        it.addPitchDetectionListener(pitchGraphModel)
                    }

                    // TODO Just here for testing
//                GlobalScope.launch {
//                    pitchGraphModel.start()
//                }

                    recordingButton.textContent = "Stop recording"
                    isRecording = true
                } else {
                    pitchDetection?.stopRecording()
                    rateInput.stopInput()


//                pitchGraphModel.stop()

                    recordingButton.textContent = "Start recording"
                    isRecording = false
                }
            })
        }
    }


    private fun setupPlayButton() {
        val playButton = document.querySelector("#btnPlay")

        if (playButton != null) {
            playButton.addEventListener("click", {

//                logger.info { "Current sequence: $currentSequence" }
//                logger.info { "Pitch sequence: $actionSequence" }

                GlobalScope.launch(Dispatchers.Default) {
                    playTargetSequenceInternal2(actionScript(actionSequence), synthesizer)
                }
            })
        } else {
            logger.error { "Play button is null" }
        }
    }


    private fun setupGenerateSequenceButton() {
        document.querySelector("#btnGenerateSequence")?.let { generateSequenceButton ->
            generateSequenceButton.addEventListener("click", {
                currentSequence = polyphonicNoteSequenceGenerator.createSequence(false)
                val (pitches, actionSequence) = computePitchSequence(currentSequence.elements)
                this.actionSequence = actionSequence
                this.rateInput.setCurrentTarget(pitches)
                pitchGraphModel.targetSequence = simpleNoteSequenceToPitchSequence(currentSequence)
            })
        }
    }


    private fun simpleNoteSequenceToPitchSequence(simpleNoteSequence: SimpleNoteSequence): List<PitchDataWithTime> {
        var idCounter = 0

        // How to go from a MIDI note number to the pitch: https://newt.phys.unsw.edu.au/jw/notes.html
        return simpleNoteSequence.transformToPitchSequence().map { pitchData ->

            PitchDataWithTime(
                (440 * 2.0.pow((pitchData.pitch - 69) / 12.0)).toFloat(),
                1.0f,
                pitchData.timeOn.toLong(),
                idCounter++
            )
        }
    }

    private fun setupShowTargetSequenceButton() {
        document.querySelector("#btnShowTargetSequence")?.let { generateSequenceButton ->
            generateSequenceButton.addEventListener("click", {
                console.log("Test23")
                pitchGraphModel.toggleTargetSequenceShowing()
            })
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

    WebPitchApp.start()


}


