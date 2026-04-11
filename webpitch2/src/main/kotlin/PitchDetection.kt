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
        logger.info { "[DEBUG_LOG] startRecording() called" }
        val constraints: dynamic = Any()
        constraints.audio = true
        constraints.video = false

        // Setting up a media stream from the microphone
        logger.info { "[DEBUG_LOG] Requesting getUserMedia" }
        window.navigator.mediaDevices.getUserMedia(constraints)
            .then { newMediaStream: dynamic ->
                logger.info { "[DEBUG_LOG] getUserMedia successful, stream: $newMediaStream" }
                mediaStream = newMediaStream
                GlobalScope.launch {
                    try {
                        logger.info { "[DEBUG_LOG] Launching processAudio coroutine" }
                        processAudio(newMediaStream)
                    } catch (e: Exception) {
                        logger.error(e) { "[DEBUG_LOG] Error in processAudio coroutine" }
                    }
                }
            }
            .catch { throwable ->
                logger.error(throwable) {
                    "[DEBUG_LOG] Exception when trying to process audio stream"
                }
            }
    }

    @JsExport.Ignore
    suspend fun urlFromFiles(files: List<String>): String {
        logger.info { "[DEBUG_LOG] urlFromFiles() called with $files" }
        val promises = files
            .map { file ->
                logger.info { "[DEBUG_LOG] Fetching file: $file" }
                window.fetch(file)
                    .then { response ->
                        if (!response.ok) {
                            logger.error { "[DEBUG_LOG] Failed to fetch $file: ${response.statusText} (status: ${response.status})" }
                            throw Exception("Failed to fetch $file: ${response.statusText}")
                        }
                        logger.info { "[DEBUG_LOG] Fetch $file successful, reading text" }
                        response.text()
                    }
            }

        logger.info { "[DEBUG_LOG] Waiting for all file fetches to complete" }
        val fileContents: Array<out String> =
            (Promise.all(promises.toTypedArray()) as Promise<Array<out String>>).await()
        logger.info { "[DEBUG_LOG] Fetched all files, contents size: ${fileContents.size}" }

        val blobUrl: String = try {
            logger.info { "[DEBUG_LOG] Creating Blob for AudioWorklet" }
            val allText = (arrayOf("var exports = {};") + fileContents).joinToString("")
            Blob(
                arrayOf(allText),
                BlobPropertyBag("application/javascript")
            ).let { URL.createObjectURL(it) }
        } catch (e: Exception) {
            logger.error(e) { "[DEBUG_LOG] Error creating Blob" }
            throw e
        }
        logger.info { "[DEBUG_LOG] Created Blob URL: $blobUrl" }
        return blobUrl
    }

    @JsExport.Ignore
    suspend fun processAudio(stream: dynamic) {
        logger.info { "[DEBUG_LOG] processAudio() called with stream: $stream" }
        logger.info { "[DEBUG_LOG] Audio context: $audioContext, state: ${audioContext.state}" }

        if (!stream.active) {
            logger.error { "[DEBUG_LOG] Microphone stream is not active" }
            return
        }

        if (audioContext.state == "closed") {
            logger.info { "[DEBUG_LOG] Re-creating AudioContext" }
            audioContext = js("new AudioContext();")
        } else if (audioContext.state == "suspended") {
            logger.info { "[DEBUG_LOG] Resuming AudioContext" }
            try {
                audioContext.resume().await()
                logger.info { "[DEBUG_LOG] AudioContext resumed, state: ${audioContext.state}" }
            } catch (e: Exception) {
                logger.error(e) { "[DEBUG_LOG] Error resuming AudioContext" }
            }
        }

        logger.info { "[DEBUG_LOG] Creating media stream source" }
        try {
            microphoneNode = (audioContext.asDynamic()).createMediaStreamSource(stream)
            logger.info { "[DEBUG_LOG] Set up microphone node: $microphoneNode" }
        } catch (e: Exception) {
            logger.error(e) { "[DEBUG_LOG] Error creating media stream source" }
        }

        setupPitchDetectionTest()
    }


    @JsExport.Ignore
    private suspend fun setupPitchDetectionTest() {
        logger.info { "[DEBUG_LOG] setupPitchDetectionTest() started" }
        // The variable is used in the JavaScript-code
        val bytes = 8 + 4 * Float32Array.BYTES_PER_ELEMENT
        val sharedArrayBuffer = js("new SharedArrayBuffer(bytes);")
        // The variable is used in the JavaScript-code
        val fileContents = urlFromFiles(listOf("essentiaModule.js"))
        logger.info { "[DEBUG_LOG] Initializing InternalAudioBuffer" }
        val audioBuffer = InternalAudioBuffer(sharedArrayBuffer)

        logger.info { "[DEBUG_LOG] Checking AudioWorklet support" }
        val worklet = (audioContext.asDynamic()).audioWorklet
        if (worklet == undefined || worklet == null) {
            logger.error { "[DEBUG_LOG] AudioWorklet is not supported in this browser" }
            throw Exception("AudioWorklet is not supported")
        }

        logger.info { "[DEBUG_LOG] Adding module to AudioWorklet: $fileContents" }
        try {
            val addModulePromise = (audioContext.asDynamic()).audioWorklet.addModule(fileContents)
            if (addModulePromise != null && addModulePromise.then != undefined) {
                (addModulePromise as Promise<Any>).await()
            }
            logger.info { "[DEBUG_LOG] addModule successful" }
        } catch (e: Exception) {
            logger.error(e) { "[DEBUG_LOG] Error adding module to AudioWorklet" }
            throw e
        } catch (e: dynamic) {
            logger.error { "[DEBUG_LOG] Dynamic error adding module to AudioWorklet: $e" }
            throw Exception("Dynamic error: $e")
        }

        logger.info { "[DEBUG_LOG] Sample rate: ${audioContext.sampleRate}" }

        val currentAudioContext = audioContext
        try {
            val sampleRate = currentAudioContext.sampleRate
            testAudioProcessorNode =
                js("new AudioWorkletNode(currentAudioContext, 'test-audio-processor', { processorOptions: { bufferSize: 8192, sampleRate: sampleRate}});")

            logger.info { "[DEBUG_LOG] AudioWorkletNode: ${testAudioProcessorNode}" }

            val input: dynamic = Any()
            input.sab = sharedArrayBuffer

            // Set up the audio network
            logger.info { "[DEBUG_LOG] Posting message to AudioWorkletNode" }
            testAudioProcessorNode.port.postMessage(input)
            gainNode = currentAudioContext.createGain()

            gainNode?.let { gain ->
                logger.info { "[DEBUG_LOG] Connecting audio graph" }
                gain.gain.setValueAtTime(0, currentAudioContext.currentTime)

                microphoneNode?.connect(testAudioProcessorNode as AudioNode)
                testAudioProcessorNode?.connect(gainNode)
                gain.connect(currentAudioContext.destination)
            }
        } catch (e: Exception) {
            logger.error(e) { "[DEBUG_LOG] Error in setup after addModule" }
        }

        val pitchBuffer = js("new Float32Array(3);") as Float32Array

        processData = true
        logger.info { "[DEBUG_LOG] processData set to true, starting animation frame loop" }

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
                            logger.info { "[DEBUG_LOG] Dequeued data: ${pitchBuffer.asDynamic()[0]}, ${pitchBuffer.asDynamic()[1]}, ${pitchBuffer.asDynamic()[2]}" }
                            val d0: dynamic = pitchBuffer.asDynamic()[0]
                            val d1: dynamic = pitchBuffer.asDynamic()[1]
                            val pitchData = PitchData(d0 as Float, d1 as Float)
                            pitchDetectionListeners.forEach {
                                try {
                                    it.pitchData(pitchData)
                                } catch (e: Exception) {
                                    logger.error(e) { "[DEBUG_LOG] Error notifying listener" }
                                }
                            }
                        } catch (e: Exception) {
                            logger.error(e) { "[DEBUG_LOG] Error processing dequeued data" }
                        }
                    }
                } catch (e: Exception) {
                    logger.error(e) { "[DEBUG_LOG] Error in callback loop" }
                }
            }
        }
        window.requestAnimationFrame { timestamp: Any -> callback(timestamp) }
    }


    fun stopRecording() {
        logger.info { "[DEBUG_LOG] stopRecording() called" }
        processData = false
        val streamAsDynamic = mediaStream.asDynamic()
        streamAsDynamic?.getAudioTracks()?.forEach { track: dynamic ->
            track.stop()
            streamAsDynamic.removeTrack(track)
        }

        audioContext.close().then {
            logger.info { "[DEBUG_LOG] AudioContext closed" }
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