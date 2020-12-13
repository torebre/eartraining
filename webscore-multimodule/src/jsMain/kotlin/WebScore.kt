import com.github.aakira.napier.Napier
import com.kjipo.score.Accidental
import com.kjipo.score.PositionedRenderingElement
import com.kjipo.score.RenderingSequence
import com.kjipo.score.Translation
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import kotlinx.serialization.json.Json
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import kotlinx.browser.document
import kotlinx.dom.clear


class WebScore(private val scoreHandler: ScoreHandlerJavaScript,
               private val svgElementId: String = "score",
               private val allowInput: Boolean = true) {

    var activeElement: String? = null
        set(value) {
            field = value
            reload()
        }

    private var xStart = 0
    private var yStart = 0

    private var direction: Boolean? = null
    private var movementActive = false
    private val svgElement: Element
    private val idSvgElementMap = mutableMapOf<String, Element>()


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
        setupEventHandling()
        loadScore(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()))
    }

    @JsName("reload")
    fun reload() {
        val score = scoreHandler.getScoreAsJson()
        loadScore(transformJsonToRenderingSequence(score))
    }

    @JsName("setVisible")
    fun setVisible(visible: Boolean) {
        document.getElementById(svgElementId)?.setAttribute("visibility", if (visible) {
            "visible"
        } else {
            "hidden"
        })
    }

    private fun transformJsonToRenderingSequence(jsonData: String): RenderingSequence {
        return Json.decodeFromString(RenderingSequence.serializer(), jsonData)
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

        setupTouchEvents()
        setupMouseEvent()

        document.addEventListener("keydown", { event ->
            val keyboardEvent = event as KeyboardEvent

            Napier.d("Key pressed: ${keyboardEvent.keyCode}. Code: ${keyboardEvent.code}. Active element: ${activeElement}")

            handleKeyEvent(keyboardEvent.code, keyboardEvent.keyCode)
        })
    }

    private fun setupMouseEvent() {
        document.addEventListener("mousedown", { event ->
            val mouseDownEvent: dynamic = event
            movementActive = true

            console.log("Mouse down")

            xStart = mouseDownEvent.pageX
            yStart = mouseDownEvent.pageY
        })

        document.addEventListener("mouseup", { event ->
            if (!movementActive) {
                return@addEventListener
            }

            val (xDiff, yDiff) = extractDiffs(event, xStart, yStart)
            movementActive = false

            console.log("Mouse up")

            if (handleMotionUpdate(xDiff, yDiff, true)) {
                val mouseDownEvent: dynamic = event
                xStart = mouseDownEvent.pageX as Int
                yStart = mouseDownEvent.pageY as Int
            }
        })

        document.addEventListener("mousemove", { event ->
            if (!movementActive) {
                return@addEventListener
            }

            console.log("Mouse move")

            val (xDiff, yDiff) = extractDiffs(event, xStart, yStart)
            if (handleMotionUpdate(xDiff, yDiff, false)) {
                val mouseDownEvent: dynamic = event
                xStart = mouseDownEvent.pageX as Int
                yStart = mouseDownEvent.pageY as Int
            }
        })
    }

    private fun setupTouchEvents() {
        document.addEventListener("touchstart", { event ->
            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches
            movementActive = true

            if (changedTouches.length > 0) {
                xStart = changedTouches[0].pageX
                yStart = changedTouches[0].pageY
            }

            console.log("Touch start")
        })

        document.addEventListener("touchend", { event ->
            if (!movementActive) {
                return@addEventListener
            }

            console.log("Touch end")

            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches
            movementActive = false

            if (changedTouches.length > 0) {
                val xStop = changedTouches[0].pageX
                val yStop = changedTouches[0].pageY

                val xDiff = xStop - xStart
                val yDiff = yStop - yStart

                handleMotionUpdate(xDiff, yDiff, true)
            }
        })

        document.addEventListener("touchmove", { event ->
            if (!movementActive) {
                return@addEventListener
            }

            console.log("Touch move")

            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches

            if (changedTouches.length > 0) {
                val xStop = changedTouches[0].pageX
                val yStop = changedTouches[0].pageY

                val xDiff = xStop - xStart
                val yDiff = yStop - yStart

                if (handleMotionUpdate(xDiff, yDiff, false)) {
                    xStart = xStop
                    yStart = yStop
                }
            }
        })
    }

    private fun extractDiffs(event: Event, xStart: Int, yStart: Int): Pair<Int, Int> {
        val eventWithPageInformation: dynamic = event
        val xStop = eventWithPageInformation.pageX as Int
        val yStop = eventWithPageInformation.pageY as Int
        val xDiff = xStop - xStart
        val yDiff = yStop - yStart

        return Pair(xDiff, yDiff)
    }

    private fun handleMotionUpdate(xDiff: Int, yDiff: Int, motionStopped: Boolean): Boolean {
        if (direction == null) {
            direction = xDiff > yDiff
        }

        val oldDirection = direction
        if (motionStopped) {
            direction = null
        }

        oldDirection?.also {
            if (it) {
                if (xDiff < -HORIZONTAL_STEP) {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, true)
                    }
                    highLightActiveElement()
                    return true
                } else if (xDiff > HORIZONTAL_STEP) {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, false)
                    }
                    highLightActiveElement()
                    return true
                }
            } else {
                if (yDiff < -VERTICAL_STEP) {
                    activeElement?.let {
                        // Up
                        scoreHandler.moveNoteOneStep(it, true)
                        regenerateSvg()
                        highLightActiveElement()
                    }
                    return true
                } else if (yDiff > VERTICAL_STEP) {
                    activeElement?.let {
                        // Down
                        scoreHandler.moveNoteOneStep(it, false)
                        regenerateSvg()
                        highLightActiveElement()
                    }
                    return true
                }
            }
        }

        return false
    }

    private fun handleKeyEvent(code: String, keyCode: Int) {
        when (code) {
            "ArrowUp" -> activeElement?.let {
                // Up
                scoreHandler.moveNoteOneStep(it, true)
                regenerateSvg()
                highLightActiveElement()
            }

            "ArrowDown" -> activeElement?.let {
                // Down
                scoreHandler.moveNoteOneStep(it, false)
                regenerateSvg()
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
                    scoreHandler.insertNote(it, keyCode - 48)
                    regenerateSvg()
                    highLightActiveElement()
                }
            }

            "Numpad1", "Numpad2", "Numpad3", "Numpad4" -> {
                activeElement?.let {
                    scoreHandler.updateDuration(it, keyCode - 96)
                    regenerateSvg()
                    highLightActiveElement()
                }
            }

            "KeyN" -> {
                activeElement?.let {
                    activeElement = scoreHandler.switchBetweenNoteAndRest(it, keyCode)
                    regenerateSvg()
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
                    regenerateSvg()

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
                    return
                }
                scoreHandler.toggleExtra(activeElement!!, Accidental.FLAT)
                regenerateSvg()
            }

            "KeyS" -> {
                if (activeElement == null) {
                    return
                }
                scoreHandler.toggleExtra(activeElement!!, Accidental.SHARP)
                regenerateSvg()
            }
        }
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

    private fun regenerateSvg() {
        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
    }

}