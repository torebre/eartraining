package graph

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGElement
import kotlin.math.log2

class PitchGraph(svgElementId: String, private val pitchGraphModel: PitchGraphModel) : PitchGraphModelListener {
    private val xMinCoordinate = 0
    private val yMinCoordinate = 0
    private val xMaxCoordinate = 500
    private val yMaxCoordinate = 500

    private val width = 500

    private val yMinValue = log2(PITCH_CLASS_FREQUENCIES.first())
    private val yMaxValue = log2(PITCH_CLASS_FREQUENCIES.last())

    private val svgElement: SVGElement

    private var startTime: Long = 0
    private var lastTimePoint: Long = 0

//    private var previousXCoord: Int = -1
//    private var previousYCoord: Int = -1

    private var zoomLevel = 100000
    private var stepSize = zoomLevel / xMaxCoordinate
    private var pixelStepSize = 1

    private var outsideRangeShiftInMilliseconds = 1000

    private var rangeInMilliseconds = 10000


    private var windowStartAbsolute = 0L
    private var windowsEndAbsolute = 0L

    private var idPointMap = mutableMapOf<Int, Element>()

    private var idsPointMap = mutableMapOf<Pair<Int, Int>, Element>()


    private var axisRightXStart = xMaxCoordinate - 100

    private val logger = KotlinLogging.logger {}


    init {
        svgElement = document.getElementById(svgElementId) as SVGElement
        svgElement.setAttribute("viewBox", "$xMinCoordinate $yMinCoordinate $xMaxCoordinate $yMaxCoordinate")
        drawPitchAxis()

        pitchGraphModel.addPitchDataListener(this)
    }


    private fun transformToX(timestamp: Long): Int {
        return ((timestamp - windowStartAbsolute).toDouble() / (windowsEndAbsolute - windowStartAbsolute) * width * pixelStepSize).toInt()
    }


    private fun transformToY(pitch: Float): Int {
        val pitchOnLogScale = log2(pitch)
        val yCoord =
            yMaxCoordinate - ((yMaxValue - pitchOnLogScale) / (yMaxValue - yMinValue)) * (yMaxCoordinate - yMinCoordinate)
        return turnYAxis(yCoord.toInt())
    }

    private fun clearAndDrawLine(idPointsToConnect: List<Int>) {
        idsPointMap.values.forEach { it.remove() }
        idsPointMap.clear()

        if (idPointsToConnect.size < 2) {
            return
        }

        var previousId = idPointsToConnect.first()
        for (id in idPointsToConnect.subList(1, idPointsToConnect.size)) {
            val previousPoint = idPointMap[previousId]
            val currentPoint = idPointMap[id]

            if (previousPoint == null || currentPoint == null) {
                previousId = id
                continue
            }

            idsPointMap[Pair(previousId, id)] = connectPointsWithLine(
                previousPoint.getAttribute("cx")!!.toInt(),
                previousPoint.getAttribute("cy")!!.toInt(),
                currentPoint.getAttribute("cx")!!.toInt(),
                currentPoint.getAttribute("cy")!!.toInt()
            )
            previousId = id
        }
    }


    private fun windowNeedsShift(): Boolean {
        return lastTimePoint > windowsEndAbsolute
    }

    private fun movePointsIfNeeded(): Boolean {
        var pointsShifted = false
        while (windowNeedsShift()) {
            pointsShifted = true
            shiftPoints()
        }
        return pointsShifted
    }

    private fun shiftPoints() {
        shiftWindowToRight()
        updateGraphBasedOnCurrentWindow()
    }

    private fun updateGraphBasedOnCurrentWindow() {
        pitchGraphModel.getPointsInRange(windowStartAbsolute, windowsEndAbsolute).let { dataPointsToShow ->
            val idsOfPointsToShow = dataPointsToShow.map { it.id }
            idPointMap.keys.filter { !idsOfPointsToShow.contains(it) }.forEach {
                // These are points that are no longer showing
                idPointMap[it]?.remove()
                idPointMap.remove(it)
            }

            dataPointsToShow.forEach { dataPoint ->
                if (idPointMap.containsKey(dataPoint.id)) {
                    // This is a data point that should still be showing
                    val existingPoint = idPointMap[dataPoint.id]
                    existingPoint?.setAttribute("cx", transformToX(dataPoint.timeStamp).toString())
                } else {
                    // This is a new point
                    idPointMap[dataPoint.id] =
                        addPointToGraph(transformToX(dataPoint.timeStamp), transformToY(dataPoint.pitch))
                }
            }

            clearAndDrawLine(idsOfPointsToShow)
        }
    }

    private fun shiftWindowToRight() {
        windowsEndAbsolute += outsideRangeShiftInMilliseconds
        windowStartAbsolute += outsideRangeShiftInMilliseconds
    }

    override fun newPitchDataReceived(pitchData: PitchDataWithTime) {

        if (startTime == 0L) {
            startTime = pitchData.timeStamp
            windowStartAbsolute = pitchData.timeStamp
            windowsEndAbsolute = windowStartAbsolute + rangeInMilliseconds
        }

        lastTimePoint = pitchData.timeStamp
        window.requestAnimationFrame { timestamp: Double -> updateWithData(pitchData) }
    }

    private fun updateWithData(pitchData: PitchDataWithTime) {
        val xCoord = transformToX(pitchData.timeStamp)
        val yCoord = transformToY(pitchData.pitch)

        idPointMap[pitchData.id] = addPointToGraph(xCoord, yCoord)
        if (!movePointsIfNeeded()) {
            // TODO This can be done more efficiently since the IDs of the points to connect have already been sent to the graph
            clearAndDrawLine(pitchGraphModel.getPointsInRange(windowStartAbsolute, windowsEndAbsolute).map { it.id })
        }
    }


    private fun turnYAxis(yCoord: Int) = yMaxCoordinate - yCoord

    override fun uncertainPitchReceived(timestamp: Long) {
        // TODO Mark in graph

    }


    private fun addPointToGraph(xCoord: Int, yCoord: Int): Element {
        return document.createElementNS(SVG_NAMESPACE_URI, "circle").also {
            with(it) {
                setAttribute("cx", "$xCoord")
                setAttribute("cy", "$yCoord")
                setAttribute("r", "2")
                setAttribute("fill", "red")
            }
        }.also {
            svgElement.appendChild(it)
        }

    }

    private fun connectPointsWithLine(xCoord: Int, yCoord: Int, previousXCoord: Int, previousYCoord: Int): Element {
        return document.createElementNS(SVG_NAMESPACE_URI, "line").also {
            with(it) {
                setAttribute("x1", "$previousXCoord")
                setAttribute("y1", "$previousYCoord")
                setAttribute("x2", "$xCoord")
                setAttribute("y2", "$yCoord")
                setAttribute("stroke", "black")
            }
        }.also {
            svgElement.appendChild(it)
        }
    }


    private fun drawPitchAxis() {
        for (pitchClassFrequency in PITCH_CLASS_FREQUENCIES) {
            logger.debug {
                "Pitch class frequency: $pitchClassFrequency. Y-coordinate: ${
                    transformToY(
                        pitchClassFrequency
                    )
                }"
            }

            val textElement = document.createElementNS(SVG_NAMESPACE_URI, "text").also {
                with(it) {
                    setAttribute("x", "$axisRightXStart")
                    setAttribute("y", "${transformToY(pitchClassFrequency)}")
                    setAttribute("class", "pitchAxis")
                    textContent = "${getPitchClosestToFrequency(pitchClassFrequency)} (${pitchClassFrequency})"
                }
            }
            svgElement.appendChild(textElement)
        }
    }


    companion object {
        internal const val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"

    }


}