import kotlin.browser.document


fun main() {
    document.querySelector("button")!!.addEventListener("click", {
        playNote()
    })
}
