import kotlin.browser.document


fun main() {
    val synthesizer = SynthesizerScript()
    var notePlaying = false

    document.querySelector("button")!!.addEventListener("click", {
        if(notePlaying) {
            synthesizer.noteOff(60)
        }
        else {
            synthesizer.noteOn(60)
        }

        notePlaying = !notePlaying
    })
}
