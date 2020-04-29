import com.github.aakira.napier.Napier
import com.kjipo.score.*
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import kotlinx.serialization.json.Json
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.KeyboardEvent
import kotlin.browser.document
import kotlin.dom.clear


class WebScore(private val scoreHandler: ScoreHandlerJavaScript, svgElementId: String = "score", private val allowInput: Boolean = true) {
    var activeElement: String? = null
        set(value) {
            field = value
            reload()
        }
    private val svgElement: Element
    private val idSvgElementMap = mutableMapOf<String, Element>()


    companion object {
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
        setupEventHandling()
        loadScore(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()))
    }

    fun reload() {
        val score = scoreHandler.getScoreAsJson()
        loadScore(transformJsonToRenderingSequence(score))
    }

    private fun transformJsonToRenderingSequence(jsonData: String): RenderingSequence {
        return Json.parse(RenderingSequence.serializer(), jsonData)
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

    fun removeHighlight(id: String) {
        idSvgElementMap[id]?.setAttribute("fill", "black")
    }

    fun setVisible(visible: Boolean) {
        document.getElementById(svgElement)?.setAttribute("visibility", if (visible) {
            "visible"
        } else {
            "hidden"
        })
    }

    private fun highLightActiveElement() {
        if (activeElement == null) {
            activeElement = scoreHandler.getIdOfFirstSelectableElement()
        }

        activeElement?.let {
            document.getElementById(it)?.setAttribute("fill", "red")
        }
    }

    private fun deactivateActiveElement() {
        activeElement?.let {
            document.getElementById(it)?.setAttribute("fill", "black")
        }
    }

    private fun setupEventHandling() {
        if (!allowInput) {
            return
        }
        var xStart = 0
        var yStart = 0

        svgElement.addEventListener("touchstart", { event ->
            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches

            if (changedTouches.length > 0) {
                xStart = changedTouches[0].pageX
                yStart = changedTouches[0].pageY
            }
        })

        svgElement.addEventListener("touchend", { event ->
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

            println("Key pressed: ${keyboardEvent.keyCode}. Code: ${keyboardEvent.code}. Active element: ${activeElement}")

            when (keyboardEvent.code) {
                "ArrowUp" -> activeElement?.let {
                    // Up
                    scoreHandler.moveNoteOneStep(it, true)
                    generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                    highLightActiveElement()
                }

                "ArrowDown" -> activeElement?.let {
                    // Down
                    scoreHandler.moveNoteOneStep(it, false)
                    generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                    highLightActiveElement()
                }

                "ArrowLeft" -> {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, true)
                    }
                    highLightActiveElement()
                }

                "ArrowRight" -> {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, false)
                    }
                    highLightActiveElement()
                }

                "Digit1", "Digit2", "Digit3", "Digit4", "Digit5" -> {
                    activeElement?.let {
                        scoreHandler.insertNote(it, keyboardEvent.keyCode - 48)
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }

                "Numpad1", "Numpad2", "Numpad3", "Numpad4" -> {
                    activeElement?.let {
                        scoreHandler.updateDuration(it, keyboardEvent.keyCode - 96)
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }

                "KeyN" -> {
                    activeElement?.let {
                        activeElement = scoreHandler.switchBetweenNoteAndRest(it, keyboardEvent.keyCode)
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                        highLightActiveElement()
                    }
                }

                "Delete" -> {
                    activeElement?.let {
                        deactivateActiveElement()
                        activeElement = null

                        var neighbouringElement = scoreHandler.getNeighbouringElement(it, true)
                        if (neighbouringElement == null) {
                            neighbouringElement = scoreHandler.getNeighbouringElement(it, false)
                        }
                        scoreHandler.deleteElement(it)
                        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)

                        if (neighbouringElement != null) {
                            activeElement = neighbouringElement
                            highLightActiveElement()
                        } else {
                            scoreHandler.getIdOfFirstSelectableElement()
                        }

                    }
                }

                "KeyF" -> {
                    if (activeElement == null) {
                        return@addEventListener
                    }
                    scoreHandler.toggleExtra(activeElement!!, Accidental.FLAT)
                    generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                }

                "KeyS" -> {
                    if (activeElement == null) {
                        return@addEventListener
                    }
                    scoreHandler.toggleExtra(activeElement!!, Accidental.SHARP)
                    generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
                }


            }
        })
    }


    private fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
        svgElement.clear()
        svgElement.setAttribute("viewBox",
                "${renderingSequence.viewBox.xMin} ${renderingSequence.viewBox.yMin} ${renderingSequence.viewBox.xMax} ${renderingSequence.viewBox.yMax}")

        Napier.d("Generating SVG. Number of render groups: ${renderingSequence.renderGroups.size}")

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
            val elementToAddRenderingElementsTo = if (renderGroup.transform != null) {
                val translation = renderGroup.transform ?: Translation(0, 0)
                svgElement.ownerDocument?.let {
                    val groupingElement = it.createElementNS(SVG_NAMESPACE_URI, "g")
                    groupingElement.setAttribute("transform", "translate(${translation.xShift}, ${translation.yShift})")

                    svgElement.appendChild(groupingElement)
                    groupingElement
                }
            } else {
                svgElement
            }

            elementToAddRenderingElementsTo?.let {
                addPositionRenderingElements(renderGroup.renderingElements, it)
            }

        }
        highLightActiveElement()
    }


    private fun addPositionRenderingElements(renderingElements: Collection<PositionedRenderingElement>, element: Element) {
        for (renderingElement in renderingElements) {

            val groupClass = renderingElement.groupClass
            val extraAttributes = if (groupClass != null) {
                mapOf(Pair("class", groupClass))
            } else {
                emptyMap()
            }

            if (renderingElement.typeId != null) {
                renderingElement.typeId?.let { typeId ->
                    addPathUsingReference(element, typeId, renderingElement, extraAttributes)
                    idSvgElementMap.put(renderingElement.id, element)
                }
            } else {
                for (pathInterface in renderingElement.renderingPath) {
                    addPath(element,
                            transformToPathString(translateGlyph(pathInterface, renderingElement.xPosition, renderingElement.yPosition)),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill,
                            extraAttributes)?.let { pathElement ->
                        idSvgElementMap.put(renderingElement.id, pathElement)
                    }
                }
            }
        }
    }

    private fun addPath(node: Node, path: String, strokeWidth: Int, id: String, fill: String? = null, extraAttributes: Map<String, String> = emptyMap()): Element? {
        return node.ownerDocument?.let { ownerDocument ->
            val path1 = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            fill?.let { path1.setAttribute("fill", it) }
            path1.setAttribute("id", id)
            path1.setAttribute("stroke-width", strokeWidth.toString())

            extraAttributes.forEach {
                path1.setAttribute(it.key, it.value)
            }

            node.appendChild(path1)
            path1
        }
    }

    private fun addPathUsingReference(node: Node, reference: String, positionedRenderingElement: PositionedRenderingElement, extraAttributes: Map<String, String> = emptyMap()) {
        node.ownerDocument?.let { ownerDocument ->
            val useTag = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "use")
            useTag.setAttribute("href", "#$reference")
            useTag.setAttribute("id", positionedRenderingElement.id)

            extraAttributes.forEach {
                useTag.setAttribute(it.key, it.value)
            }

            if (positionedRenderingElement.xTranslate != 0
                    || positionedRenderingElement.yTranslate != 0) {
                val groupingElement = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "g")
                groupingElement.setAttribute("transform", "translate(${positionedRenderingElement.xTranslate}, ${positionedRenderingElement.yTranslate})")
                groupingElement.appendChild(useTag)
                node.appendChild(groupingElement)
            } else {
                node.appendChild(useTag)
            }

        }
    }

}