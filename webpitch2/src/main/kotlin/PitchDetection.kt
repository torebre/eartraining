import com.kjipo.attemptprocessor.PitchData
import audio.InternalAudioBuffer
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import io.github.oshai.kotlinlogging.KotlinLogging
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Float32Array
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
    private var mediaStream: Any? = null

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
            .then { newMediaStream: dynamic ->
                mediaStream = newMediaStream
                GlobalScope.launch {
                    try {
                        processAudio(newMediaStream)
                    } catch (e: Exception) {
                        logger.error(e) { "Error in processAudio coroutine" }
                    }
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
                        if (!response.ok) {
                            throw Exception("Failed to fetch $file: ${response.statusText}")
                        }
                        response.text()
                    }
            }

        val fileContents: Array<out String> =
            (Promise.all(promises.toTypedArray()) as Promise<Array<out String>>).await()

        val blobUrl: String = try {
            val allText = (arrayOf("var exports = {};") + fileContents).joinToString("")
            Blob(
                arrayOf(allText),
                BlobPropertyBag("application/javascript")
            ).let { URL.createObjectURL(it) }
        } catch (e: Exception) {
            throw e
        }
        return blobUrl
    }

    @JsExport.Ignore
    suspend fun processAudio(stream: dynamic) {
        if (!stream.active) {
            return
        }

        if (audioContext.state == "closed") {
            audioContext = js("new AudioContext();")
        } else if (audioContext.state == "suspended") {
            try {
                audioContext.resume().await()
            } catch (e: Exception) {
                logger.error(e) { "Error resuming AudioContext" }
            }
        }

        try {
            microphoneNode = (audioContext.asDynamic()).createMediaStreamSource(stream)
        } catch (e: Exception) {
            logger.error(e) { "Error creating media stream source" }
        }

        setupPitchDetectionTest()
    }


    @JsExport.Ignore
    private suspend fun setupPitchDetectionTest() {
        // The variable is used in the JavaScript-code
        val bytes = 8 + 4 * Float32Array.BYTES_PER_ELEMENT
        val sharedArrayBuffer = js("new SharedArrayBuffer(bytes);")
        // The variable is used in the JavaScript-code
        val fileContents = urlFromFiles(listOf("essentiaModule.js"))
        val audioBuffer = InternalAudioBuffer(sharedArrayBuffer)

        val worklet = (audioContext.asDynamic()).audioWorklet
        if (worklet == undefined || worklet == null) {
            throw Exception("AudioWorklet is not supported")
        }

        try {
            val addModulePromise = (audioContext.asDynamic()).audioWorklet.addModule(fileContents)
            if (addModulePromise != null && addModulePromise.then != undefined) {
                (addModulePromise as Promise<Any>).await()
            }
        } catch (e: Exception) {
            throw e
        } catch (e: dynamic) {
            throw Exception("Dynamic error: $e")
        }

        val currentAudioContext = audioContext
        try {
            val sampleRate = currentAudioContext.sampleRate
            testAudioProcessorNode =
                js("new AudioWorkletNode(currentAudioContext, 'test-audio-processor', { processorOptions: { bufferSize: 8192, sampleRate: sampleRate}});")

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
        } catch (e: Exception) {
            logger.error(e) { "Error in setup after addModule" }
        }

        val pitchBuffer = js("new Float32Array(3);") as Float32Array

        processData = true

        fun callback(timeStamp: Any) {
            if (!processData) {
                return
            }

            // logger.info { "Bytes available for reading: ${audioBuffer.availableRead()}" }
            window.requestAnimationFrame { timestamp2: Any -> callback(timestamp2) }
            if (audioBuffer.availableRead() >= 1) {
                try {
                    val read = audioBuffer.dequeue(pitchBuffer)
                    if (read != 0) {
                        try {
                            val d0: dynamic = pitchBuffer.asDynamic()[0]
                            val d1: dynamic = pitchBuffer.asDynamic()[1]
                            val pitchData = PitchData(d0 as Float, d1 as Float)
                            pitchDetectionListeners.forEach {
                                try {
                                    it.pitchData(pitchData)
                                } catch (e: Exception) {
                                    logger.error(e) { "Error notifying listener" }
                                }
                            }
                        } catch (e: Exception) {
                            logger.error(e) { "Error processing dequeued data" }
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error in callback loop" }
                }
            }
        }
        window.requestAnimationFrame { timestamp: Any -> callback(timestamp) }
    }


    fun stopRecording() {
        processData = false
        val streamAsDynamic = mediaStream.asDynamic()
        streamAsDynamic?.getAudioTracks()?.forEach { track: dynamic ->
            track.stop()
            streamAsDynamic.removeTrack(track)
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