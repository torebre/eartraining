import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


fun showWebscore() {
    val synthesizer = SynthesizerScript()
    val webscoreShow = WebscoreShow(synthesizer)
    webscoreShow.createSequence()
    webscoreShow.createInputScore()

    document.querySelector("#playTarget")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playSequence()
        }
    })

    document.querySelector("#playInput")!!.addEventListener("click", {
        GlobalScope.launch(Dispatchers.Default) {
            webscoreShow.playInputSequence()
        }
    })

}

fun main() {
    showWebscore()
}
