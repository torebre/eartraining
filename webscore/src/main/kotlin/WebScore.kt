import com.kjipo.score.*
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

    private fun transformJsonToRenderingSequence(jsonData: String): RenderingSequence {
        return JSON.parse(RenderingSequence.serializer(), jsonData)
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
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                } else if (yDiff > 50) {
                    activeElement?.let {
                        // Down
                        scoreHandler.moveNoteOneStep(it, false)
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
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
                    generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                    highLightActiveElement()
                }
                40 -> activeElement?.let {
                    // Down
                    scoreHandler.moveNoteOneStep(it, false)
                    generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
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
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }
                97, 98, 99, 100 -> {
                    activeElement?.let {
                        scoreHandler.updateDuration(it, keyboardEvent.keyCode - 96)
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }
            }
        })
    }


    private fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
        svgElement.setAttribute("viewBox",
                "${renderingSequence.viewBox.xMin} ${renderingSequence.viewBox.yMin} ${renderingSequence.viewBox.xMax} ${renderingSequence.viewBox.yMax}")

        console.log("Generating SVG. Number of render groups: ${renderingSequence.renderGroups.size}")

        svgElement.clear()

        svgElement.ownerDocument?.let {
            val defsTag = it.createElementNS(SVG_NAMESPACE_URI, "defs")

            for (definition in renderingSequence.definitions) {
                addPath(defsTag,
                        transformToPathString(definition.value.pathElements),
                        definition.value.strokeWidth,
                        definition.key)
            }

            svgElement.appendChild(defsTag)
        }


        renderingSequence.renderGroups.forEach { renderGroup ->
            val svgElement = if (renderGroup.transform != null) {
                println("Found transformation: ${renderGroup.transform}")

                val translation = renderGroup?.transform ?: Translation(0, 0)
                svgElement.ownerDocument?.let {
                    val groupingElement = it.createElementNS(SVG_NAMESPACE_URI, "g")
                    groupingElement.setAttribute("transform", "translate(${translation.xShift}, ${translation.yShift})")

                    svgElement.appendChild(groupingElement)
                    groupingElement
                }
            } else {
                svgElement
            }

            svgElement?.let {

                println("Adding element")

                addPositionRenderingElements(renderGroup.renderingElements, it)
            }

        }
        highLightActiveElement()
    }


    private fun addPositionRenderingElements(renderingElements: Collection<PositionedRenderingElement>, element: Element) {
        for (renderingElement in renderingElements) {
            if (renderingElement.duration != null) {
                renderingElement.duration?.let { duration ->
                    addPathUsingReference(element, duration.name, renderingElement.id, mapOf(Pair("y", renderingElement.yPosition.toString())))
                    idSvgElementMap.put(renderingElement.id, element)
                }
            } else {
                for (pathInterface in renderingElement.renderingPath) {
                    addPath(element,
                            transformToPathString(translateGlyph(pathInterface, renderingElement.xPosition, renderingElement.yPosition)),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill)?.let { element ->
                        idSvgElementMap.put(renderingElement.id, element)
                    }
                }
            }
        }
    }

    private fun addPath(node: Node, path: String, strokeWidth: Int, id: String, fill: String? = null): Element? {
        return node.ownerDocument?.let { ownerDocument ->
            val path1 = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            path1.setAttribute("stroke", STROKE_COLOUR)
            fill?.let { path1.setAttribute("fill", it) }
            path1.setAttribute("id", id)
            path1.setAttribute("stroke-width", strokeWidth.toString())

            node.appendChild(path1)
            path1
        }
    }

    private fun addPathUsingReference(node: Node, reference: String, id: String, extraAttributes: Map<String, String> = emptyMap()): Element? {
        return node.ownerDocument?.let { ownerDocument ->
            val useTag = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "use")
            useTag.setAttribute("href", "#$reference")
            useTag.setAttribute("id", id)

            extraAttributes.forEach {
                useTag.setAttribute(it.key, it.value)
            }

            node.appendChild(useTag)

            useTag
        }
    }


}