import com.kjipo.handler.MoveElement
import kotlinx.browser.document
import kotlinx.html.currentTimeMillis
import mu.KotlinLogging
import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.get
import org.w3c.dom.svg.SVGPathElement
import org.w3c.dom.svg.SVGSVGElement
import org.w3c.dom.svg.SVGUseElement


class WebScore(
    private val scoreHandler: ScoreHandlerJavaScript,
    private val svgElementId: String = "score",
    allowInput: Boolean = true
) : ScoreHandlerListener, NoteInputListener {

    private var latestId: Int = 0
    private val webscoreSvgProvider: WebscoreSvgProvider = WebscoreSvgProvider(ScoreProvider(scoreHandler))
    private val noteInput: NoteInput

    private val webscoreListeners = mutableListOf<WebscoreListener>()

    private var activeElement: String? = null

    private var xStart = 0
    private var yStart = 0

    private var direction: Boolean? = null
    private var movementActive = false
    private val svgElement: SVGSVGElement

    private var currentlyHighlightedElement: String? = null

    private var noteInputMode: WebscoreInputMode = WebscoreInputMode.MOVE

    private var mouseEventLastFired: Long = 0

    private val logger = KotlinLogging.logger {}

    companion object {
        private const val VERTICAL_STEP = 10
        private const val HORIZONTAL_STEP = 30
    }

    init {
        scoreHandler.addListener(this)
        noteInput = NoteInput(scoreHandler)
        noteInput.addNoteInputListener(this)
        val element = document.getElementById(svgElementId)

        svgElement = if (element is SVGSVGElement) {
            element
        } else {
            val createdElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")
            createdElement.id = svgElementId
            document.body?.appendChild(createdElement)
            createdElement as SVGSVGElement
        }

        if (allowInput) {
            setupEventHandling()
        }
        loadScore()
    }

    fun addListener(webscoreListener: WebscoreListener) = webscoreListeners.add(webscoreListener)

    fun removeListener(webscoreListener: WebscoreListener) = webscoreListeners.remove(webscoreListener)

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

        latestId = scoreHandler.getLatestId()
        highlightElement(activeElement)
    }

    fun highlight(ids: Collection<String>) {
        ids.forEach { highlight(it) }
    }

    fun highlight(id: String, removeExistingHighlight: Boolean = true): Boolean {
        webscoreSvgProvider.getHighlightForId(id).let { elementsToHighlight ->
            if (elementsToHighlight.isEmpty()) {
                return false
            }

            if (removeExistingHighlight) {
                currentlyHighlightedElement?.let {
                    removeHighlight(it)
                }
            }

            elementsToHighlight.forEach {
                webscoreSvgProvider.getElement(it)?.classList?.add("highlight")
            }
            return true
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


    private fun highlightElementIfFound(elementId: String) {
        highlight(elementId, true).takeIf { it }?.run {
            currentlyHighlightedElement = elementId
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
            handleKeyEvent(keyboardEvent)
        })
    }

    private fun setupMouseEvent() {
        svgElement.addEventListener("mousedown", { event ->
            val mouseDownEvent: dynamic = event
            movementActive = true

            xStart = mouseDownEvent.pageX
            yStart = mouseDownEvent.pageY
        })

        svgElement.addEventListener("mouseup", { event ->
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

        svgElement.addEventListener("mousemove", { event ->
            val currentTime = currentTimeMillis()

            if (currentTime - mouseEventLastFired > 200) {
                val eventWithPageInformation: dynamic = event
                val xStop = eventWithPageInformation.pageX as Int
                val yStop = eventWithPageInformation.pageY as Int
                highlightElementClosestToMouseCursor(xStop, yStop)
                mouseEventLastFired = currentTime
            }

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
        svgElement.addEventListener("touchstart", { event ->
            val touchEvent: dynamic = event
            val changedTouches = touchEvent.changedTouches
            movementActive = true

            if (changedTouches.length > 0) {
                xStart = changedTouches[0].pageX
                yStart = changedTouches[0].pageY
            }
        })

        svgElement.addEventListener("touchend", { event ->
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

        svgElement.addEventListener("touchmove", { event ->
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
                    activeElement = activeElement?.let { id ->
                        scoreHandler.getNeighbouringElement(id, true)
                    }
                    highlightElement(activeElement)
                    return true
                } else if (xDiff > HORIZONTAL_STEP) {
                    deactivateActiveElement()
                    activeElement = activeElement?.let { id ->
                        scoreHandler.getNeighbouringElement(id, false)
                    }
                    highlightElement(activeElement)
                    return true
                }
            } else {
                if (yDiff < -VERTICAL_STEP) {
                    activeElement?.let { id ->
                        // Up
                        scoreHandler.applyOperation(MoveElement(id, true))
                    }
                    return true
                } else if (yDiff > VERTICAL_STEP) {
                    activeElement?.let { id ->
                        // Down
                        scoreHandler.moveNoteOneStep(id, false)
                    }
                    return true
                }
            }
        }

        return false
    }

    private fun handleKeyEvent(keyboardEvent: KeyboardEvent) {
        // TODO Fix strings to that they match with the keys

        when (keyboardEvent.code) {
            "KeyM" -> {
                noteInput.clear()
                if (noteInputMode != WebscoreInputMode.MOVE) {
                    noteInputMode = WebscoreInputMode.MOVE
                    webscoreListeners.forEach { it.noteInputMode(noteInputMode) }
                }
            }

            "KeyN" -> {
                noteInput.clear()
                noteInput.insertNoteNotRest(true)
                if (noteInputMode != WebscoreInputMode.NOTE) {
                    noteInputMode = WebscoreInputMode.NOTE
                    webscoreListeners.forEach { it.noteInputMode(noteInputMode) }
                }
            }

            "KeyR" -> {
                noteInput.clear()
                noteInput.insertNoteNotRest(false)
                if (noteInputMode != WebscoreInputMode.REST) {
                    noteInputMode = WebscoreInputMode.REST
                    webscoreListeners.forEach { it.noteInputMode(noteInputMode) }
                }
            }

            "KeyE" -> {
                noteInput.clear()
                if (noteInputMode != WebscoreInputMode.EDIT) {
                    noteInputMode = WebscoreInputMode.EDIT
                    webscoreListeners.forEach { it.noteInputMode(noteInputMode) }
                }

            }

            else -> {
                when (noteInputMode) {
                    WebscoreInputMode.NOTE, WebscoreInputMode.REST -> {
                        noteInput.processInput(keyboardEvent)
                    }

                    WebscoreInputMode.MOVE -> {
                        processMoveCommand(keyboardEvent.code)
                    }

                    WebscoreInputMode.EDIT -> {
                        processEditModeCommand(keyboardEvent.code)
                    }
                }
            }
        }
    }

    private fun processMoveCommand(code: String) {
        when (code) {
            "KeyH" -> {
                activeElement = scoreHandler.getNeighbouringElement(activeElement, true)
                highlightElement(activeElement)
            }

            "KeyL" -> {
                activeElement = scoreHandler.getNeighbouringElement(activeElement, false)
                highlightElement(activeElement)
            }
        }
    }

    private fun processEditModeCommand(code: String) {
        when (code) {
            "KeyK" -> activeElement?.let {
                scoreHandler.moveNoteOneStep(it, true)
            }

            "KeyJ" -> activeElement?.let {
                scoreHandler.moveNoteOneStep(it, false)
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

                    if (neighbouringElement != null) {
                        activeElement = neighbouringElement
                        highlightElement(activeElement)
                    } else {
                        scoreHandler.getIdOfFirstSelectableElement()
                    }
                }
            }

        }

    }

    private fun generateSvgData(svgElement: Element) {
        webscoreSvgProvider.generateSvgData(svgElement)
        highlightElement(activeElement)
    }

    override fun scoreUpdated(updateId: Int) {
        logger.debug { "Latest ID: ${latestId}. Update ID: ${updateId}. Change set: ${scoreHandler.getChangeSet(latestId)}" }

        scoreHandler.getChangeSet(latestId)?.let { changeSet ->
            webscoreSvgProvider.updateSvg(changeSet, svgElement)
        } ?: generateSvgData(svgElement)
        latestId = scoreHandler.getLatestId()
    }


    private fun highlightElementClosestToMouseCursor(xPosition: Int, yPosition: Int) {
        val svgRect = svgElement.createSVGRect()
        with(svgRect) {
            x = maxOf(xPosition.toDouble().minus(10), 0.0)
            y = maxOf(yPosition.toDouble().minus(10), 0.0)
            width = 20.0
            height = 20.0
        }

        val intersectionList = svgElement.getIntersectionList(svgRect, svgElement)
        val numberOfNodes = intersectionList.length
        (0..numberOfNodes).forEach { index ->
            intersectionList[index]?.let { node ->
                when (node) {
                    is SVGUseElement -> {
                        highlightElementIfFound(node.id)
                    }

                    is SVGPathElement -> {
                        highlightElementIfFound(node.id)
                    }

                }

            }

        }

    }

    override fun currentStep(currentStep: NoteInput.NoteInputStep) {
        webscoreListeners.forEach { it.currentStep(currentStep) }
    }

}
