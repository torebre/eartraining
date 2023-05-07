import kotlinx.browser.document
import mu.KotlinLogging
import org.w3c.dom.Element


class WebScoreView(
    private val webscoreSvgProvider: WebscoreSvgProvider,
    svgElementId: String = "score"
) {
    private var xStart = 0
    private var yStart = 0

    private var direction: Boolean? = null
    private var movementActive = false
    private val svgElement: Element

    private val logger = KotlinLogging.logger {}

    companion object {
        private const val VERTICAL_STEP = 10
        private const val HORIZONTAL_STEP = 30

        const val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"
    }

    init {
        val element = document.getElementById(svgElementId)

        svgElement = if ("svg" == element?.tagName) {
            element
        } else {
            val createdElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")
            createdElement.id = svgElementId
            document.body?.appendChild(createdElement)
            createdElement
        }

        loadScore()
    }

    private fun loadScore() {
        generateSvgData(svgElement)
    }

    private fun generateSvgData(svgElement: Element) {
        webscoreSvgProvider.generateSvgData(svgElement)
    }

}
