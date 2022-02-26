import kotlinx.browser.document
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

fun main() {
    val callback = { pitchData: PitchData ->
        logger.info { "Got pitch data: ${pitchData}" }
    }

    // TODO Use a class instead of an object
    var pitchDetection: PitchDetection? = null
    var isRecording = false

    document.querySelector("#btnToggleRecording")?.let { recordingButton ->
        logger.info { "Adding event listener" }

        recordingButton.addEventListener("click", {
            if (!isRecording) {
                if(pitchDetection == null) {
                   pitchDetection = PitchDetection
                }
                pitchDetection?.startRecording()
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
