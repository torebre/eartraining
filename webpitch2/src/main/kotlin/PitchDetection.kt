import com.kjipo.attemptprocessor.PitchData
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import io.github.oshai.kotlinlogging.KotlinLogging
import org.khronos.webgl.Float32Array
import org.w3c.dom.mediacapture.MediaStream
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import webaudioapi.AudioContext
import webaudioapi.AudioNode
import webaudioapi.GainNode
import webaudioapi.MediaStreamAudioSourceNode
import kotlin.js.Promise


@OptIn(kotlin.js.ExperimentalJsExport::class)
@JsExport
class PitchDetection {
    @JsName("audioContext")
    var audioContext: AudioContext = js("new AudioContext();")

    @JsExport.Ignore
    private var processData = false
    @JsExport.Ignore
    private var mediaStream: MediaStream? = null
    @JsExport.Ignore
    private var microphoneNode: MediaStreamAudioSourceNode? = null
    @JsExport.Ignore
    private var gainNode: GainNode? = null


    // TODO It is an AudioWorkletNode, but where is the code for the interface?
    @JsExport.Ignore
    private var testAudioProcessorNode: dynamic = null

    @JsExport.Ignore
    private val pitchDetectionListeners = mutableListOf<PitchDetectionListener>()

    @JsExport.Ignore
    private val logger = KotlinLogging.logger {}

    @JsName("PitchDetection")
    constructor() {
        logger.info { "Initializing PitchDetection" }
    }


    fun startRecording() {
        val constraints: dynamic = Any()
        constraints.audio = true
        constraints.video = false

        // Setting up a media stream from the microphone
        window.navigator.mediaDevices.getUserMedia(constraints)
            .then { newMediaStream ->
                mediaStream = newMediaStream
                GlobalScope.launch {
                    processAudio(newMediaStream)
                }
            }
            .catch { throwable ->
                logger.error(throwable) {
                    "Exception when trying to process audio stream"
                }
            }
    }

    @JsExport.Ignore
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
                Blob(
                    arrayOf((arrayOf("var exports = {};") + stringPromises).joinToString("")),
                    BlobPropertyBag("application/javascript")
                ).let { URL.createObjectURL(it) }
            }.await()
    }

    @JsExport.Ignore
    suspend fun processAudio(stream: MediaStream) {
        logger.info { "Setting up audio processing: $stream" }
        logger.info { "Audio context: $audioContext" }

        if (!stream.active) {
            logger.error { "Microphone stream is not active" }
            return
        }

        if (audioContext.state == "closed") {
            audioContext = js("new AudioContext();")
        } else if (audioContext.state == "suspended") {
            audioContext.resume()
        }

        logger.info { "Setting up audio stream from microphone" }
        microphoneNode = audioContext.createMediaStreamSource(stream as webaudioapi.MediaStream)

        logger.info { "Set up microphone node" }

        setupPitchDetectionTest()
    }


    @JsExport.Ignore
    private suspend fun setupPitchDetectionTest() {
        // The variable is used in the JavaScript-code
        val bytes = 8 + 4 * Float32Array.BYTES_PER_ELEMENT
        val sharedArrayBuffer =
            js("new SharedArrayBuffer(bytes);")
        // The variable is used in the JavaScript-code
        val fileContents = urlFromFiles(listOf("essentiaModule.js"))
        // TODO InternalAudioBuffer is loaded on the index.html page, that is why it is available here. It would be better if the code could access in a different way
        val audioBuffer = js("new InternalAudioBuffer(sharedArrayBuffer);")

        val addModulePromise: Promise<Any> =
            (audioContext.asDynamic()).audioWorklet.addModule(fileContents) as Promise<Any>

        logger.info { "Sample rate: ${audioContext.sampleRate}" }

        val currentAudioContext = audioContext
        addModulePromise.then {
            val sampleRate = currentAudioContext.sampleRate
            testAudioProcessorNode =
                js("new AudioWorkletNode(currentAudioContext, 'test-audio-processor', { processorOptions: { bufferSize: 8192, sampleRate: sampleRate}});")

            logger.info { "AudioWorkletNode: ${testAudioProcessorNode}" }

            val input: dynamic = Any()
            input.sab = sharedArrayBuffer

            // Set up the audio network
            testAudioProcessorNode.port.postMessage(input)
            gainNode = currentAudioContext.createGain()

            gainNode?.let { gain ->
                gain.gain.setValueAtTime(0, currentAudioContext.currentTime)

                microphoneNode?.connect(testAudioProcessorNode as AudioNode)
                testAudioProcessorNode?.connect(gainNode)
                gain.connect(currentAudioContext.destination)
            }
        }
            .catch({ throwable -> logger.info(throwable, { "Exception when setting up audio graph" }) })

        val pitchBuffer = js("new Float32Array(3);")

        processData = true
//            while(processData) {
//                // TODO This is a very tight loop, add a sleep?
//
//                if (audioReader.available_read() >= 1) {
//                    val read = audioReader.dequeue(pitchBuffer)
//                    if (read !== 0) {
//                        logger.info { "main: ${pitchBuffer[0]}, ${pitchBuffer[1]}, ${pitchBuffer[2]}" }
//                    }
//                }
//            }

        fun callback(timeStamp: Any) {
            if (!processData) {
                return
            }

            logger.info { "Bytes available for reading: ${audioBuffer.availableRead()}" }
            window.requestAnimationFrame { timestamp: Any -> callback(timestamp) }
            if (audioBuffer.availableRead() >= 1) {
                val read = audioBuffer.dequeue(pitchBuffer)
                if (read !== 0) {
                    logger.info { "main: ${pitchBuffer[0]}, ${pitchBuffer[1]}, ${pitchBuffer[2]}" }
                    val pitchData = PitchData(pitchBuffer[0] as Float, pitchBuffer[1] as Float)
                    pitchDetectionListeners.forEach { it.pitchData(pitchData) }
                }
            }
        }
        window.requestAnimationFrame { timestamp: Any -> callback(timestamp) }
    }


    fun stopRecording() {
        processData = false
        mediaStream?.getAudioTracks()?.forEach {
            it.stop()
            mediaStream?.removeTrack(it)
        }

        audioContext.close().then {
            microphoneNode?.disconnect()
            testAudioProcessorNode?.disconnect()
            gainNode?.disconnect()

            microphoneNode = null
            testAudioProcessorNode = null
            gainNode = null
        }

    }


    fun addPitchDetectionListener(pitchDetectionListener: PitchDetectionListener) =
        pitchDetectionListeners.add(pitchDetectionListener)

    fun removePitchDetectionListener(pitchDetectionListener: PitchDetectionListener) =
        pitchDetectionListeners.remove(pitchDetectionListener)

}