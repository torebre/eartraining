import kotlinx.browser.document
import kotlinx.browser.window
//import kotlinx.coroutines.awaitAll
import org.w3c.dom.get
import org.w3c.dom.mediacapture.MediaStreamConstraints
import webaudioapi.AudioContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.js.Promise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import kotlin.coroutines.suspendCoroutine


suspend fun <T> Promise<T>.await(): T = suspendCoroutine { cont ->
    then({ cont.resume(it) }, { cont.resumeWithException(it) })
}

suspend fun startRecording() {
//    val mediaConstraints: MediaStreamConstraints = object : MediaStreamConstraints {
//        override var audio = false
//        override var video = false
//    }

    val constraints: dynamic = Any()
    constraints.audio = true
    constraints.video = false
    val userMedia = window.navigator.mediaDevices.getUserMedia(constraints).await()

    console.log("User media: ${userMedia}")
}


fun main() {
    document.write("Starting")

    val audioContext = window.get("AudioContext") as AudioContext

//    val audioContext = if(window.AudioContext == null) {
//       window.AudioContext()
//    }
//    else {
//        window.webkitAudioContext()
//    }

    console.log("Audio context: ${audioContext}")


    GlobalScope.launch(Dispatchers.Default) {
        startRecording()
    }

}