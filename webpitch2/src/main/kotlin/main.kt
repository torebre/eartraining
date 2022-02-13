import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.get
import webaudioapi.AudioContext

fun main() {
    document.write("Hello, world!")

    val audioContext = window.get("AudioContext") as AudioContext



//    val audioContext = if(window.AudioContext == null) {
//       window.AudioContext()
//    }
//    else {
//        window.webkitAudioContext()
//    }

    console.log("Audio context: ${audioContext}")

}