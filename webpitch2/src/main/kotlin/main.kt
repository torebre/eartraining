import graph.PitchGraph
import graph.PitchGraphModel
import kotlinx.browser.document
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

fun main() {
    val callback = { pitchData: PitchData ->
        logger.info { "Got pitch data: ${pitchData}" }
    }

    // TODO Why does it not work when PitchDetection is a class?
    var pitchDetection: PitchDetection? = null
    var isRecording = false

    val pitchGraphModel = PitchGraphModel()
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
                pitchDetection?.startRecording()
                pitchDetection?.addPitchDetectionListener(pitchGraphModel)
                recordingButton.textContent = "Stop recording"
                isRecording = true
            } else {
                pitchDetection?.stopRecording()
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
