import graph.PitchGraph
import graph.PitchGraphModel
import graph.RandomPitchGraphModel
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel


private val logger = KotlinLogging.logger {}

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    // TODO Why does it not work when PitchDetection is a class?
//    var pitchDetection: PitchDetection? = null
    var isRecording = false

//    val pitchGraphModel = PitchGraphModel()
    val pitchGraphModel = RandomPitchGraphModel()
    val pitchGraph = PitchGraph("pitchGraph", pitchGraphModel)

    document.querySelector("#btnToggleRecording")?.let { recordingButton ->
        logger.info { "Adding event listener" }

        recordingButton.addEventListener("click", {
            if (!isRecording) {
//                if (pitchDetection == null) {
//                    pitchDetection = PitchDetection.also {
//                        it.addPitchDetectionListener(object : PitchDetectionListener {
//                            override fun pitchData(pitchData: PitchData) {
//                                logger.info { "Pitch: ${pitchData.pitch}. Certainty: ${pitchData.certainty}" }
//                                setTextForChildNode("#pitchLabel", pitchData.pitch.toString())
//                                setTextForChildNode("#certaintyLabel", pitchData.certainty.toString())
//                            }
//                        })
//                    }
//                    pitchDetection = PitchDetection
//                }
                // TODO Comment back in
//                pitchDetection?.startRecording()
//                pitchDetection?.addPitchDetectionListener(pitchGraphModel)

                // TODO Just here for testing
                GlobalScope.launch {
                    pitchGraphModel.start()
                }

                recordingButton.textContent = "Stop recording"
                isRecording = true
            } else {
                // TODO Comment back in
//                pitchDetection?.stopRecording()

                pitchGraphModel.stop()

                recordingButton.textContent = "Start recording"
                isRecording = false
            }
        })
    }

}


fun setTextForChildNode(idSelector: String, textContent: String) {
    document.querySelector(idSelector)?.let { element ->
        element.firstChild?.let {
            it.textContent = textContent
        }
    }
}
