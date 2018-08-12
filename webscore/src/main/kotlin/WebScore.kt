import com.kjipo.handler.ScoreHandlerInterface
import com.kjipo.score.RenderingSequence
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import kotlinx.serialization.json.JSON
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.dom.clear


class WebScore(var scoreHandler: ScoreHandlerInterface) {
    val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"
    val divScoreElement: Element? = document.getElementById("score")
    private val svgElement: Element
    var activeElement: String? = null
    val idSvgElementMap = mutableMapOf<String, Element>()

    init {
        console.log("Setting up SVG")
        svgElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")
        setupSvg()
        loadScore(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()))
    }

    fun loadScoreHandler(scoreHandler: ScoreHandlerInterface) {
        this.scoreHandler = scoreHandler
        activeElement = scoreHandler.getIdOfFirstSelectableElement()
        reload()
    }

    fun reload() {
        val score = scoreHandler.getScoreAsJson()
        loadScore(transformJsonToRenderingSequence(score))
    }

    fun loadScoreFromJson(jsonData: String) {
        // TODO Here for testing
        transformJsonToRenderingSequence(JSON.parse(jsonData))
    }


    private fun transformJsonToRenderingSequence(jsonData: String): RenderingSequence {
        return JSON.parse(jsonData)
    }


    fun loadScore(renderingSequence: RenderingSequence) {
        generateSvgData(renderingSequence, svgElement)
        if(activeElement == null) {
            activeElement = scoreHandler.getIdOfFirstSelectableElement()
        }
        highLightActiveElement()
    }


    fun highlight(id: String) {
        idSvgElementMap[id]?.setAttribute("fill", "red")
    }

    private fun highLightActiveElement() {
        activeElement?.let {
            document.getElementById(it)?.setAttribute("fill", "red")
        }
    }

    private fun deactivateActiveElement() {
        activeElement?.let {
            document.getElementById(it)?.setAttribute("fill", "yellow")
        }
    }


    private fun setupSvg() {
        document.addEventListener("keydown", { event ->
            val keyboardEvent = event as KeyboardEvent

            println("Key pressed: ${keyboardEvent.keyCode}. Active element: ${activeElement}")

            when (keyboardEvent.keyCode) {
                38 -> activeElement?.let {
                    // Up
                    scoreHandler.moveNoteOneStep(it, true)
                    generateSvgData(JSON.parse(scoreHandler.getScoreAsJson()), svgElement)
                    highLightActiveElement()
                }
                40 -> activeElement?.let {
                    // Down
                    scoreHandler.moveNoteOneStep(it, false)
                    generateSvgData(JSON.parse(scoreHandler.getScoreAsJson()), svgElement)
                    highLightActiveElement()
                }
                37 -> {
                    deactivateActiveElement()
                    activeElement = if (activeElement == null) {
                        activeElement
                    } else {
                        scoreHandler.getNeighbouringElement(activeElement!!, true)
                    }
                    highLightActiveElement()
                }
                39 -> {
                    deactivateActiveElement()
                    activeElement = if (activeElement == null) {
                        activeElement
                    } else {
                        scoreHandler.getNeighbouringElement(activeElement!!, false)
                    }
                    highLightActiveElement()
                }
            }
        })


        // TODO Set proper view box
        svgElement.setAttribute("viewBox", "0 0 1000 1000")

        divScoreElement?.appendChild(svgElement)
    }


    private fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
        val xStart = 50
        val yStart = 100

        console.log("Generating SVG. Rendering sequence: ${renderingSequence.renderingElements.size}")

        svgElement.clear()

        renderingSequence.renderingElements.forEach {
            //            if (it.glyphData != null) {
//
//                // TODO Why does using references not work here?
//
//                // TODO The ID setup will only work if there is one path
//
//                it.glyphData?.let { glyphData ->
//                    for (pathInterface in it.renderingPath) {
//                        val path = addPath(svgElement,
//                                transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
//                                pathInterface.strokeWidth,
//                                it.id)
//
//                        println("Path: ${path?.id ?: "none"}")
//
//                        path?.let { element ->
//                            idSvgElementMap.put(it.id, element)
//                        }
//                    }
//                }
//            }
//            else {
            for (pathInterface in it.renderingPath) {
                addPath(svgElement,
                        transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
                        pathInterface.strokeWidth,
                        it.id)?.let { element ->
                    idSvgElementMap.put(it.id, element)
                }


            }
//            }
        }

        highLightActiveElement()
    }


    private fun addPath(node: Node, path: String, strokeWidth: Int, id: String?): Element? {
        return node.ownerDocument?.let {
            val path1 = it.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            path1.setAttribute("stroke", "blue")
            path1.setAttribute("fill", "yellow")
            id?.let { path1.setAttribute("id", it) }
            path1.setAttribute("stroke-width", strokeWidth.toString())

            node.appendChild(path1)
            path1
        }
    }


    private fun addPathUsingReference(node: Node, reference: String, x: Int, y: Int, id: String?) {
        node.ownerDocument?.let {
            val useTag = it.createElementNS(SVG_NAMESPACE_URI, "use")
            useTag.setAttribute("xlink:href", "#$reference")
            useTag.setAttribute("x", x.toString())
            useTag.setAttribute("y", y.toString())
            node.appendChild(useTag)
            if (id != null) {
                useTag.setAttribute("id", id)
            }
        }
    }


}