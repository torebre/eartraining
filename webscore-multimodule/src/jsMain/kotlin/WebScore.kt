import com.kjipo.handler.InsertNote
import com.kjipo.handler.InsertRest
import com.kjipo.handler.ScoreHandlerUtilities
import kotlinx.browser.document
import kotlinx.dom.appendText
import mu.KotlinLogging
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent


class WebScore(
    private val scoreHandler: ScoreHandlerJavaScript,
    private val svgElementId: String = "score",
    allowInput: Boolean = true,
    private val debugLabelId: String? = null
) {

    private val webscoreSvgProvider: WebscoreSvgProvider = WebscoreSvgProvider(ScoreProvider(scoreHandler))

    var activeElement: String? = null
        set(value) {
            field = value

            writeDebug("Active element", value)

            // reload()
        }

    private var xStart = 0
    private var yStart = 0

    private var direction: Boolean? = null
    private var movementActive = false
    private val svgElement: Element

    private var currentlyHighlightedElement: String? = null

    private var noteModeOn = true
        get() = field

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

        if (allowInput) {
            setupEventHandling()
        }
        loadScore()
    }

    // TODO Is there no difference between load and reload?
    @JsName("reload")
    fun reload() = loadScore()

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

    private fun loadScore() {
        generateSvgData(svgElement)
        if (activeElement == null) {
            activeElement = scoreHandler.getIdOfFirstSelectableElement()
        }
        highlightElement(activeElement)
    }

    fun highlight(ids: Collection<String>) {
        ids.forEach { highlight(it) }
    }

    fun highlight(id: String) {

        logger.debug { "Activating highlighting: $id" }


        webscoreSvgProvider.getHighlightForId(id).forEach {
            webscoreSvgProvider.getElement(it)?.classList?.add("highlight")
        }

        // TODO For some reason an exception is thrown if the addClass-method is used
//            it.addClass("highlight")
//        }
    }

    fun removeHighlight(ids: Collection<String>) = ids.forEach { removeHighlight(it) }

    fun removeHighlight(id: String) {
        webscoreSvgProvider.getHighlightForId(id).forEach {
            webscoreSvgProvider.getElement(it)?.classList?.remove("highlight")
        }
    }


    private fun highlightElement(elementId: String?) {
        if (elementId != null) {
            currentlyHighlightedElement?.let {
                removeHighlight(it)
            }
            highlight(elementId)
            currentlyHighlightedElement = elementId
        } else {
            currentlyHighlightedElement?.let {
                removeHighlight(it)
            }
            currentlyHighlightedElement = null
        }
    }

    private fun deactivateActiveElement() {
        activeElement?.let {
            removeHighlight(it)
        }
    }

    private fun setupEventHandling() {
        setupTouchEvents()
        setupMouseEvent()

        document.addEventListener("keydown", { event ->
            val keyboardEvent = event as KeyboardEvent

            logger.debug { "Key pressed: ${keyboardEvent.keyCode}. Code: ${keyboardEvent.code}. Active element: ${activeElement}" }

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
                    highlightElement(activeElement)
                    return true
                } else if (xDiff > HORIZONTAL_STEP) {
                    deactivateActiveElement()
                    activeElement = activeElement?.let {
                        scoreHandler.getNeighbouringElement(it, false)
                    }
                    highlightElement(activeElement)
                    return true
                }
            } else {
                if (yDiff < -VERTICAL_STEP) {
                    activeElement?.let {
                        // Up
                        scoreHandler.moveNoteOneStep(it, true)
                        regenerateSvg()
                        highlightElement(activeElement)
                    }
                    return true
                } else if (yDiff > VERTICAL_STEP) {
                    activeElement?.let {
                        // Down
                        scoreHandler.moveNoteOneStep(it, false)
                        regenerateSvg()
                        highlightElement(activeElement)
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
                highlightElement(activeElement)
            }

            "ArrowDown" -> activeElement?.let {
                // Down
                scoreHandler.moveNoteOneStep(it, false)
                regenerateSvg()
                highlightElement(activeElement)
            }

            "ArrowLeft" -> {
                activeElement = scoreHandler.getNeighbouringElement(activeElement, true)
                highlightElement(activeElement)
            }

            "ArrowRight" -> {
                activeElement = scoreHandler.getNeighbouringElement(activeElement, false)
                highlightElement(activeElement)
            }

            "Digit1", "Digit2", "Digit3", "Digit4", "Digit5" -> {
                val duration = getDuration(keyCode - 48)
                if (noteModeOn) {
                    scoreHandler.applyOperation(
                        InsertNote(
                            activeElement,
                            60,
                            duration
                        )
                    )
                } else {
                    scoreHandler.applyOperation(InsertRest(activeElement, duration))
                }
                regenerateSvg()
                highlightElement(activeElement)
            }

            "Numpad1", "Numpad2", "Numpad3", "Numpad4" -> {
                activeElement?.let {
                    scoreHandler.updateDuration(it, getDuration(keyCode - 96))
                    regenerateSvg()
                    highlightElement(activeElement)
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
                        highlightElement(activeElement)
                    } else {
                        scoreHandler.getIdOfFirstSelectableElement()
                    }
                }
            }

            "M" -> {
                noteModeOn != noteModeOn
            }

        }
    }

    private fun generateSvgData(svgElement: Element) {
        webscoreSvgProvider.generateSvgData(svgElement)
        highlightElement(activeElement)
    }

    private fun regenerateSvg() {
        generateSvgData(svgElement)
    }

    private fun writeDebug(tag: String, message: String?) {
        debugLabelId?.let {
            document.getElementById(it)?.appendText("$tag: $message")
        }
    }
}
