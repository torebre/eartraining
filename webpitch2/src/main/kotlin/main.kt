import com.kjipo.midi.SynthesizerScript
import com.kjipo.midi.playTargetSequenceInternal
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.actionScript
import com.kjipo.scoregenerator.computePitchSequence
import graph.PitchGraph
import graph.PitchGraphModel
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel


object WebPitchApp {

    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()

    private var isRecording = false

    private val logger = KotlinLogging.logger {}


    fun start() {
        setupPlayButton()


        // TODO Why does it not work when PitchDetection is a class?
        var pitchDetection: PitchDetection? = null

        val pitchGraphModel = PitchGraphModel()
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
                                }
                            })
                        }
                        pitchDetection = PitchDetection
                    }
                    // TODO Comment back in
                    pitchDetection?.startRecording()
                    pitchDetection?.addPitchDetectionListener(pitchGraphModel)

                    // TODO Just here for testing
//                GlobalScope.launch {
//                    pitchGraphModel.start()
//                }

                    recordingButton.textContent = "Stop recording"
                    isRecording = true
                } else {
                    // TODO Comment back in
                    pitchDetection?.stopRecording()

//                pitchGraphModel.stop()

                    recordingButton.textContent = "Start recording"
                    isRecording = false
                }
            })
        }
    }


    private fun setupPlayButton() {
        val synthesizer = SynthesizerScript()
        val simpleNoteSequence = polyphonicNoteSequenceGenerator.createSequence(false)
        val (_, actionSequence) = computePitchSequence(simpleNoteSequence.elements)
        val playButton = document.querySelector("#btnPlay")

        if (playButton != null) {
            playButton.addEventListener("click", {
                GlobalScope.launch(Dispatchers.Default) {
                    playTargetSequenceInternal(actionScript(actionSequence), synthesizer)
                }
            })
        } else {
            logger.error { "Play button is null" }
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


