import com.github.aakira.napier.Napier
import com.kjipo.score.Accidental
import com.kjipo.score.RenderingSequence
import kotlinx.browser.document
import kotlinx.serialization.json.Json
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent


class WebScore(
    private val scoreHandler: ScoreHandlerJavaScript,
    private val svgElementId: String = "score",
    private val allowInput: Boolean = true
) {

    private val webscoreSvgProvider: WebscoreSvgProvider = WebscoreSvgProvider(scoreHandler)

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
//    private val idSvgElementMap = mutableMapOf<String, Element>()


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
        document.getElementById(svgElementId)?.setAttribute(
            "visibility", if (visible) {
                "visible"
            } else {
                "hidden"
            }
        )
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

    fun highlight(ids: Collection<String>) = ids.forEach { highlight(it) }

    fun highlight(id: String) {
        webscoreSvgProvider.getElement(id)?.setAttribute("fill", "red")
    }

    fun removeHighlight(ids: Collection<String>) = ids.forEach { removeHighlight(it) }

    fun removeHighlight(id: String) {
        webscoreSvgProvider.getElement(id)?.setAttribute("fill", "black")
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

            xStart = mouseDownEvent.pageX
            yStart = mouseDownEvent.pageY
        })

        document.addEventListener("mouseup", { event ->
            if (!movementActive) {
                return@addEventListener
            }

            val (xDiff, yDiff) = extractDiffs(event, xStart, yStart)
            movementActive = false

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
        webscoreSvgProvider.generateSvgData(renderingSequence, svgElement)
        highLightActiveElement()
    }


    private fun regenerateSvg() {
        generateSvgData(transformJsonToRenderingSequence(scoreHandler.getScoreAsJson()), svgElement)
    }

}