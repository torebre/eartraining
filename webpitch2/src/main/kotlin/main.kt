import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.w3c.dom.mediacapture.MediaStream
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import webaudioapi.AudioContext
import webaudioapi.AudioNode
import webaudioapi.MediaStreamAudioSourceNode
import kotlin.js.Promise


object MicrophoneInput {
    @JsName("audioContext")
    var audioContext: AudioContext = js("new AudioContext();")

    private var processData = false
    private var microphone: MediaStreamAudioSourceNode? = null

    // TODO It is an AudioWorkletNode, but where is the code for the interface?
    private var pitchNode: dynamic = Any()
    private val logger = KotlinLogging.logger {}


    fun startRecording() {
        val constraints: dynamic = Any()
        constraints.audio = true
        constraints.video = false

        // Setting up a media stream from the microphone
        window.navigator.mediaDevices.getUserMedia(constraints)
            .then { mediaStream ->
                GlobalScope.launch {
                    processAudio(mediaStream)
                }
            }
            .catch { throwable ->
                logger.error(throwable) {
                    "Exception when trying to process audio stream"
                }
            }
    }

    suspend fun urlFromFiles(files: List<String>): String {
        val promises = files
            .map { file ->
                window.fetch(file)
                    .then { response ->
                        response.text()
                    }
            }

        val fileContents = Promise.all(promises.toTypedArray()).await()

        return Promise.all(fileContents)
            .then { stringPromises ->
                val input = (arrayOf("var exports = {};") + stringPromises).joinToString("")
                val blob = Blob(arrayOf(input), BlobPropertyBag("application/javascript"))
                URL.createObjectURL(blob)
            }.await()
    }

    suspend fun processAudio(stream: MediaStream) {
        if (stream.active) {
            if (audioContext.state == "closed") {
                logger.error { "AudioContext state is closed" }
            } else if (audioContext.state == "suspended") {
                audioContext.resume()
            }

            microphone = audioContext.createMediaStreamSource(stream as webaudioapi.MediaStream)
            val sharedArrayBuffer =
                js("RingBuffer.getStorageForCapacity(3, Float32Array);") // capacity: three float32 values [pitch, confidence, rms]
            val rb = js("new RingBuffer(sharedArrayBuffer, Float32Array);")
            val audioReader = js("new AudioReader(rb);")
            val fileContents = urlFromFiles(
                listOf(
                    "essentia-wasm.umd.js",
                    "essentia.js-core.umd.js",
                    "pitchyinprob-processor.js",
                    "index.js"
                )
            )

            val addModulePromise: Promise<Any> =
                js("webpitch2.MicrophoneInput.audioContext.audioWorklet.addModule(fileContents);")

            addModulePromise.then {
                pitchNode =
                    js("new AudioWorkletNode(webpitch2.MicrophoneInput.audioContext, 'pitchyinprob-processor', { processorOptions: { bufferSize: 8192, sampleRate: webpitch2.MicrophoneInput.audioContext.sampleRate}});")

                val input: dynamic = Any()
                input.sab = sharedArrayBuffer

                // Set up the audio network
                pitchNode.port.postMessage(input)
                val gain = audioContext.createGain()

                // TODO Probably need to create a js-file with the pitch detection Essentia code that can be loaded here
//                    val pitchDetection = PitchDetection
//                    js("registerProcessor(\"pitch-detection-processor\", pitchDetection);")
//                    val pitchNode =
//                        js("new AudioWorkletNode(webpitch2.MicrophoneInput.audioContext, 'pitchyinprob-processor', { processorOptions: { bufferSize: 8192, sampleRate: webpitch2.MicrophoneInput.audioContext.sampleRate}});")

                gain.gain.setValueAtTime(0, audioContext.currentTime)

                microphone?.connect(pitchNode as AudioNode)
                pitchNode?.connect(gain)
                gain.connect(audioContext.destination)
            }
                .catch({ throwable -> logger.info(throwable, { "Exception when setting up audio graph" }) })

            val pitchBuffer = js("new Float32Array(3);")

            processData = true
//            while(processData) {
//                // TODO This is a very tight loop, add a sleep?
//                if (audioReader.available_read() >= 1) {
//                    val read = audioReader.dequeue(pitchBuffer)
//                    if (read !== 0) {
//                        logger.info { "main: ${pitchBuffer[0]}, ${pitchBuffer[1]}, ${pitchBuffer[2]}" }
//                    }
//                }
//            }

            fun callback(timeStamp: Any) {
                logger.debug { "Bytes available for reading: ${audioReader.available_read()}" }
                window.requestAnimationFrame { timestamp: Any -> callback(timestamp) }
                if (audioReader.available_read() >= 1) {
                    val read = audioReader.dequeue(pitchBuffer)
                    if (read !== 0) {
                        logger.info { "main: ${pitchBuffer[0]}, ${pitchBuffer[1]}, ${pitchBuffer[2]}" }
                    }
                }
            }
            window.requestAnimationFrame { timestamp: Any -> callback(timestamp) }


        }
    }


    fun stopRecording() {
        // TODO

        processData = false


    }

}

fun main() {
    document.write("Starting")

    MicrophoneInput.startRecording()

    document.querySelector("btnToggleRecording")?.let { recordingButton ->
        recordingButton.addEventListener("click", {
            MicrophoneInput.startRecording()
        })
    }

}
