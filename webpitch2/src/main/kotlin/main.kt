import kotlinx.browser.document
import kotlinx.browser.window
import mu.KotlinLogging
import org.w3c.dom.Window
import org.w3c.dom.mediacapture.MediaStream
import webaudioapi.AudioContext
import webaudioapi.AudioNode
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise


object MicrophoneInput {

    @JsName("audioContext")
    var audioContext: AudioContext = js("new AudioContext();")

    private val logger = KotlinLogging.logger {}


    fun startRecording() {
//    val mediaConstraints: MediaStreamConstraints = object : MediaStreamConstraints {
//        override var audio = false
//        override var video = false
//    }

        val constraints: dynamic = Any()
        constraints.audio = true
        constraints.video = false
//    val userMedia = window.navigator.mediaDevices.getUserMedia(constraints).await()
//    console.log("User media: ${userMedia}")

        val userMedia = window.navigator.mediaDevices.getUserMedia(constraints)
            .then { mediaStream -> processAudio(mediaStream) }
            .catch { throwable ->
                logger.error(throwable) {
                    "Exception when trying to process audio stream"
                }

            }

    }


    fun processAudio(stream: MediaStream) {
        logger.info { "Test23" }

        if (stream.active) {
            logger.info { "Test24: ${audioContext.state}" }

            if (audioContext.state == "closed") {
//                audioContext = AudioContext()
                logger.warn { "AudioContext state is closed" }
            } else if (audioContext.state == "suspended") {
                audioContext.resume()
            }

            logger.info { "Test25: ${audioContext}" }

//            audioContext.createMediaStreamDestination()

            val microphone = audioContext.createMediaStreamSource(stream as webaudioapi.MediaStream)

            logger.info { "Test26: Secure context: ${js("window.isSecureContext")}" }



//            val essentiaWasmUmdJs = js("require('./essentia-wasm.umd.js')")


//        logger.info { "Test27: ${js("exports.webpitch2.audioContext")}" }

            js("new SharedArrayBuffer(1);")

            logger.info { "Test50" }
            val sab =
                js("RingBuffer.getStorageForCapacity(3, Float32Array);") // capacity: three float32 values [pitch, confidence, rms]
            val rb = js("new RingBuffer(sab, Float32Array);")
            val audioReader = js("new AudioReader(rb);")

            val promise: Promise<Any> =
                js("URLFromFiles([\"essentia-wasm.umd.js\", \"essentia.js-core.umd.js\", \"pitchyinprob-processor.js\", \"index.js\"])")

            promise.then { code ->

                logger.info { js("console.log(\"Test60: \" +webpitch2);") }

                val promise2: Promise<Any> = js("webpitch2.MicrophoneInput.audioContext.audioWorklet.addModule(code);")

                promise2.then {
                    logger.info { "Test35. Sample rate: ${audioContext.sampleRate}" }

                    val pitchNode =
                        js("new AudioWorkletNode(webpitch2.MicrophoneInput.audioContext, 'pitchyinprob-processor', { processorOptions: { bufferSize: 8192, sampleRate: webpitch2.MicrophoneInput.audioContext.sampleRate}});")

                    logger.info { "Test36: ${pitchNode}" }

                    val input: dynamic = Any()
                    input.sab = sab

                    pitchNode.port.postMessage(input)

                    logger.info { "Test37" }

                    val gain = audioContext.createGain()
                    gain.gain.setValueAtTime(0, audioContext.currentTime)

                    microphone.connect(pitchNode as AudioNode)
                    pitchNode.connect(gain)
                    gain.connect(audioContext.destination)


                }
                    .catch({ throwable -> logger.info(throwable, { "Exception when setting up audio graph" }) })

            }
                .catch({ throwable -> logger.info(throwable, { "Exception when loading module" }) })


//        js("webpitch2.audioContext.audioWorklet.addModule(\"essentia-wasm.umd.js\");")

            logger.info { "Test30" }

            val pitchBuffer = js("new Float32Array(3);")
            fun callback(timeStamp: Any) {
                logger.info { "Test70: ${audioReader.available_read()}"}
                window.requestAnimationFrame { timestamp: Any -> callback(timestamp)  }
                if (audioReader.available_read() >= 1) {
                    val read = audioReader.dequeue(pitchBuffer);

                    if (read !== 0) {
                        console.log("main: ", pitchBuffer[0], pitchBuffer[1], pitchBuffer[2]);
                        // elapsed = timestamp - animationStart;
                        // animationStart = timestamp;
                        // console.info(elapsed);
                    }
                }
            }

            window.requestAnimationFrame { timestamp: Any -> callback(timestamp)}

//        audioContext.audioWorklet.addModule("essentia2-wasm.umd.js")
//            .then({ module ->
//                logger.info { "Loaded module" }
//            })
//            .catch { throwable -> logger.error(throwable, { "Exception when trying to load module" }) }
        }


    }




}

suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then({ cont.resume(it) }, { cont.resumeWithException(it) })
}


fun main() {
    document.write("Starting")

    MicrophoneInput.startRecording()


//    val audioContext = if(window.AudioContext == null) {
//       window.AudioContext()
//    }
//    else {
//        window.webkitAudioContext()
//    }


//    GlobalScope.launch(Dispatchers.Default) {
//        startRecording()
//    }

}