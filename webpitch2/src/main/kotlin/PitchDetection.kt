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
import webaudioapi.GainNode
import webaudioapi.MediaStreamAudioSourceNode
import kotlin.js.Promise


data class PitchData(val pitch: Float, val certainty: Float)

object PitchDetection { // callback: (pitchData: PitchData) -> Unit) {
    @JsName("audioContext")
    var audioContext: AudioContext = js("new AudioContext();")

    private var processData = false
    private var mediaStream: MediaStream? = null
    private var microphoneNode: MediaStreamAudioSourceNode? = null
    private var gainNode: GainNode? = null


    // TODO It is an AudioWorkletNode, but where is the code for the interface?
    private var testAudioProcessorNode: dynamic = Any()
    private val logger = KotlinLogging.logger {}


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

        // TODO Comment back in pitch detection
//        setupPitchDetection()

//        addTestAudioProcessor()

        setupPitchDetectionTest()
    }

    private suspend fun setupPitchDetection() {
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
            js("webpitch2.PitchDetection.audioContext.audioWorklet.addModule(fileContents);")

        logger.info { "Sample rate: ${audioContext.sampleRate}" }

        addModulePromise.then {
            testAudioProcessorNode =
                js("new AudioWorkletNode(webpitch2.PitchDetection.audioContext, 'pitchyinprob-processor', { processorOptions: { bufferSize: 8192, sampleRate: webpitch2.PitchDetection.audioContext.sampleRate}});")

            val input: dynamic = Any()
            input.sab = sharedArrayBuffer

            // Set up the audio network
            testAudioProcessorNode.port.postMessage(input)
            gainNode = audioContext.createGain()

            gainNode?.let { gain ->
                gain.gain.setValueAtTime(0, audioContext.currentTime)

                microphoneNode?.connect(testAudioProcessorNode as AudioNode)
                testAudioProcessorNode?.connect(gainNode)
                gain.connect(audioContext.destination)
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

    private suspend fun addTestAudioProcessor() {
//        val fileContents = urlFromFiles(listOf("audio-worker.js"))
        val fileContents = urlFromFiles(listOf("essentiaModule.js"))
//        val fileContents = urlFromFiles(listOf("audio-worker2.js"))

        val addModulePromise: Promise<Any> =
            js("webpitch2.PitchDetection.audioContext.audioWorklet.addModule(fileContents);")

        // TODO Give buffer size as argument, not a hardcoded value
        addModulePromise.then {
            testAudioProcessorNode =
                js("new AudioWorkletNode(webpitch2.PitchDetection.audioContext, 'test-audio-processor', {processorOptions: { bufferSize: 8192, sampleRate: webpitch2.PitchDetection.audioContext.sampleRate, }});")

            gainNode = audioContext.createGain()

            gainNode?.let { gain ->
                gain.gain.setValueAtTime(0, audioContext.currentTime)

                microphoneNode?.connect(testAudioProcessorNode as AudioNode)
                testAudioProcessorNode?.connect(gainNode)
                gain.connect(audioContext.destination)
            }


        }
            .catch({ throwable -> logger.info(throwable, { "Exception when setting up audio graph" }) })
    }



    private suspend fun setupPitchDetectionTest() {
        val sharedArrayBuffer =
            js("RingBuffer.getStorageForCapacity(3, Float32Array);") // capacity: three float32 values [pitch, confidence, rms]
        val rb = js("new RingBuffer(sharedArrayBuffer, Float32Array);")
        val audioReader = js("new AudioReader(rb);")
        val fileContents = urlFromFiles(listOf("essentiaModule.js"))

        val addModulePromise: Promise<Any> =
            js("webpitch2.PitchDetection.audioContext.audioWorklet.addModule(fileContents);")

        logger.info { "Sample rate: ${audioContext.sampleRate}" }

        addModulePromise.then {
            testAudioProcessorNode =
                js("new AudioWorkletNode(webpitch2.PitchDetection.audioContext, 'test-audio-processor', { processorOptions: { bufferSize: 8192, sampleRate: webpitch2.PitchDetection.audioContext.sampleRate}});")

            val input: dynamic = Any()
            input.sab = sharedArrayBuffer

            // Set up the audio network
            testAudioProcessorNode.port.postMessage(input)
            gainNode = audioContext.createGain()

            gainNode?.let { gain ->
                gain.gain.setValueAtTime(0, audioContext.currentTime)

                microphoneNode?.connect(testAudioProcessorNode as AudioNode)
                testAudioProcessorNode?.connect(gainNode)
                gain.connect(audioContext.destination)
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

            logger.info { "Bytes available for reading: ${audioReader.available_read()}" }
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


    fun stopRecording() {
        processData = false
        mediaStream?.getAudioTracks()?.forEach {
            it.stop()
            mediaStream?.removeTrack(it)
        }

        audioContext.close().then {
            microphoneNode?.disconnect()
            testAudioProcessorNode.disconnect()
            gainNode?.disconnect()

            microphoneNode = null
            testAudioProcessorNode = null
            gainNode = null
        }

    }

}