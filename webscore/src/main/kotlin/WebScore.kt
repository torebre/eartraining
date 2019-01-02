import com.kjipo.score.RenderingSequence
import com.kjipo.score.STROKE_COLOUR
import com.kjipo.score.Transform
import com.kjipo.svg.GlyphData
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import kotlinx.serialization.json.JSON
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.dom.clear


class WebScore(var scoreHandler: ScoreHandlerJavaScript) {
    val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"
    private val svgElement: Element
    var activeElement: String? = null
    val idSvgElementMap = mutableMapOf<String, Element>()

    init {
        val element = document.getElementById("score")

        svgElement = if ("svg" == element?.tagName) {
            element
        } else {
            val createdElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")
            document.body?.appendChild(createdElement)
            createdElement
        }

        setupSvg()
        loadScore(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()))
    }

    fun loadScoreHandler(scoreHandler: ScoreHandlerJavaScript) {
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
        if (activeElement == null) {
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
        var xStart = 0
        var yStart = 0

        document.addEventListener("touchstart", { event ->
            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches

            if (changedTouches.length > 0) {
                xStart = changedTouches[0].pageX
                yStart = changedTouches[0].pageY
            }
        })

        document.addEventListener("touchend", { event ->
            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches

            if (changedTouches.length > 0) {
                val xStop = changedTouches[0].pageX
                val yStop = changedTouches[0].pageY

                val xDiff = xStop - xStart
                val yDiff = yStop - yStart

                if (xDiff < -50) {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, true)
                    }
                    highLightActiveElement()
                } else if (xDiff > 50) {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, false)
                    }
                    highLightActiveElement()
                }

                if (yDiff < -50) {
                    activeElement?.let {
                        // Up
                        scoreHandler.moveNoteOneStep(it, true)
                        generateSvgData(JSON.parse(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                } else if (yDiff > 50) {
                    activeElement?.let {
                        // Down
                        scoreHandler.moveNoteOneStep(it, false)
                        generateSvgData(JSON.parse(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }
            }
        })

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
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, true)
                    }
                    highLightActiveElement()
                }
                39 -> {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, false)
                    }
                    highLightActiveElement()
                }
                49, 50, 51, 52 -> {
                    activeElement?.let {
                        scoreHandler.insertNote(it, keyboardEvent.keyCode - 48)
                        generateSvgData(JSON.parse(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }
                97, 98, 99, 100 -> {
                    activeElement?.let {
                        scoreHandler.updateDuration(it, keyboardEvent.keyCode - 96)
                        generateSvgData(JSON.parse(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }
            }
        })
    }


    private fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
        svgElement.setAttribute("viewBox",
                "${renderingSequence.viewBox.xMin} ${renderingSequence.viewBox.yMin} ${renderingSequence.viewBox.xMax} ${renderingSequence.viewBox.yMax}")


        val defsMap = mutableMapOf<String, GlyphData>()

        console.log("Generating SVG. Rendering sequence: ${renderingSequence.renderingElements.size}")

        svgElement.clear()

        renderingSequence.renderingElements.forEach { renderingElement ->
            renderingElement.transform?.let {
                when (it) {
                    is Transform.Translation -> {
                        println("Found transformation: ${it}")

                    }

                }
            }

            if (renderingElement.glyphData != null) {
                val glyphData = renderingElement.glyphData!!
                defsMap.put(glyphData.name, glyphData)

                println("Glyph data: ${renderingElement.glyphData}")

                // TODO Why does using references not work here?

                // TODO The ID setup will only work if there is one path

                renderingElement.glyphData?.let { glyphData ->
                    for (pathInterface in renderingElement.renderingPath) {
                        val path = addPath(svgElement,
                                transformToPathString(translateGlyph(pathInterface, renderingElement.xPosition, renderingElement.yPosition)),
                                pathInterface.strokeWidth,
                                renderingElement.id,
                                pathInterface.fill)

                        println("Path: ${path?.id ?: "none"}")

                        path?.let { element ->
                            idSvgElementMap.put(renderingElement.id, element)
                        }
                    }
                }
            } else {
                for (pathInterface in renderingElement.renderingPath) {
                    addPath(svgElement,
                            transformToPathString(translateGlyph(pathInterface, renderingElement.xPosition, renderingElement.yPosition)),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill)?.let { element ->
                        idSvgElementMap.put(renderingElement.id, element)
                    }


                }
            }
        }
        highLightActiveElement()
    }


    private fun addPath(node: Node, path: String, strokeWidth: Int, id: String?, fill: String): Element? {
        return node.ownerDocument?.let {
            val path1 = it.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            path1.setAttribute("stroke", STROKE_COLOUR)
            path1.setAttribute("fill", fill)
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