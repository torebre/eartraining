package graph

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Element
import org.w3c.dom.svg.SVGElement
import kotlin.Int
import kotlin.math.log2

class PitchGraph(svgElementId: String, private val pitchGraphModel: PitchGraphModel) : PitchGraphModelListener {
    private val xMinCoordinate = 0
    private val yMinCoordinate = 0
    private val xMaxCoordinate = 500
    private val yMaxCoordinate = 500

    private val width = 500

    private val yMinValue = log2(PITCH_CLASS_FREQUENCIES.first().pitch)
    private val yMaxValue = log2(PITCH_CLASS_FREQUENCIES.last().pitch)

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
    private var windowsEndAbsolute = 10000L

    private var idPointMap = mutableMapOf<Int, Element>()
    private var idsPointMap = mutableMapOf<Pair<Int, Int>, Element>()

    private var idTargetPointMap = mutableMapOf<Int, Element>()
    private var idsTargetPointMap = mutableMapOf<Pair<Int, Int>, Element>()

    private var axisRightXStart = xMaxCoordinate - 100

//    private val logger = KotlinLogging.logger {}

    class PitchCoordinateData(val pitch: Float, val noteName: String, val frequency: Float, val yCoordinate: Int)



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

    private fun clearAndDrawLine(
        idPointsToConnect: List<Int>,
        idPointMap2: MutableMap<Int, Element>,
        idsPointMap2: MutableMap<Pair<Int, Int>, Element>
    ) {
        idsPointMap2.values.forEach { it.remove() }
        idsPointMap2.clear()

        if (idPointsToConnect.size < 2) {
            return
        }

        var previousId = idPointsToConnect.first()
        for (id in idPointsToConnect.subList(1, idPointsToConnect.size)) {
            val previousPoint = idPointMap2[previousId]
            val currentPoint = idPointMap2[id]

            if (previousPoint == null || currentPoint == null) {
                previousId = id
                continue
            }

            idsPointMap2[Pair(previousId, id)] = connectPointsWithLine(
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
            showDataPoints(dataPointsToShow, idPointMap, idsPointMap, "red")
        }
    }

    private fun showDataPoints(
        dataPointsToShow: List<PitchDataWithTime>,
        idPointMap2: MutableMap<Int, Element>,
        idsPointMap2: MutableMap<Pair<Int, Int>, Element>,
        colour: String
    ) {
        val idsOfPointsToShow = dataPointsToShow.map { it.id }
        idPointMap2.keys.filter { !idsOfPointsToShow.contains(it) }.forEach {
            // These are points that are no longer showing
            idPointMap2[it]?.remove()
            idPointMap2.remove(it)
        }

        dataPointsToShow.forEach { dataPoint ->
            if (idPointMap2.containsKey(dataPoint.id)) {
                // This is a data point that should still be showing
                val existingPoint = idPointMap2[dataPoint.id]
                existingPoint?.setAttribute("cx", transformToX(dataPoint.timeStamp).toString())
            } else {
                // This is a new point
                idPointMap2[dataPoint.id] =
                    addPointToGraph(
                        transformToX(dataPoint.timeStamp),
                        transformToY(dataPoint.pitch),
                        colour
                    )
            }
        }

        clearAndDrawLine(idsOfPointsToShow, idPointMap2, idsPointMap2)
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

        idPointMap[pitchData.id] = addPointToGraph(xCoord, yCoord, "red")
        if (!movePointsIfNeeded()) {
            // TODO This can be done more efficiently since the IDs of the points to connect have already been sent to the graph
            clearAndDrawLine(
                pitchGraphModel.getPointsInRange(windowStartAbsolute, windowsEndAbsolute).map { it.id },
                idPointMap,
                idsPointMap
            )
        }
    }


    private fun turnYAxis(yCoord: Int) = yMaxCoordinate - yCoord

    override fun uncertainPitchReceived(timestamp: Long) {
        // TODO Mark in graph

    }


    override fun targetSequenceShowing(isShowing: Boolean) {
        if (isShowing) {
//            val dataPointsToShow =
//                pitchGraphModel.getTargetSequencePointsInRange(windowStartAbsolute, windowsEndAbsolute)
            val dataPointsToShow = pitchGraphModel.getTargetSequence()

            console.log("Test28: " + dataPointsToShow)

            pitchGraphModel.getTargetSequence().forEach { pitchData ->
                console.log("Test25: " + pitchData.timeStamp)
            }
            console.log("Test24: $windowStartAbsolute, $windowsEndAbsolute. Number of target sequence points: ${dataPointsToShow.size}")

            showDataPoints(
                dataPointsToShow,
                idTargetPointMap,
                idsTargetPointMap,
                "green"
            )

            console.log("Test26: " + idTargetPointMap)
            console.log("Test27: " + idsTargetPointMap)
        }
    }


    private fun addPointToGraph(xCoord: Int, yCoord: Int, colour: String): Element {
        return document.createElementNS(SVG_NAMESPACE_URI, "circle").also {
            with(it) {
                setAttribute("cx", "$xCoord")
                setAttribute("cy", "$yCoord")
                setAttribute("r", "2")
                setAttribute("fill", colour)
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
        val pitchCoordinateData = PITCH_CLASS_FREQUENCIES.map { pitchClassFrequency ->
            PitchCoordinateData(
                pitchClassFrequency.pitch,
                pitchClassFrequency.note,
                pitchClassFrequency.pitch,
                transformToY(pitchClassFrequency.pitch)
            )
        }.toList()

        pitchCoordinateData.forEachIndexed { index, pitchData ->
            val border = when (index) {
                0 -> {
                    Pair(yMaxCoordinate, (pitchData.yCoordinate + pitchCoordinateData[index + 1].yCoordinate) / 2)
                }

                pitchCoordinateData.size - 1 -> {
                    Pair((pitchData.yCoordinate + pitchCoordinateData[index - 1].yCoordinate) / 2, yMinCoordinate)
                }

                else -> {
                    Pair(
                        (pitchData.yCoordinate + pitchCoordinateData[index - 1].yCoordinate) / 2,
                        (pitchData.yCoordinate + pitchCoordinateData[index + 1].yCoordinate) / 2
                    )
                }
            }

            //console.log("Test30: " + border)

            drawBackgroundLines(border, if (index % 2 == 0) "green" else "blue")
        }

        pitchCoordinateData.forEach { pitchData ->
            val textElement = document.createElementNS(SVG_NAMESPACE_URI, "text").also {
                with(it) {
                    setAttribute("x", "$axisRightXStart")
                    setAttribute("y", "${pitchData.yCoordinate}")
                    setAttribute("class", "pitchAxis")
                    textContent = "${pitchData.noteName} (${formatPitchFrequence(pitchData.frequency)})"
                }
            }
            svgElement.appendChild(textElement)
        }

    }


    private fun drawBackgroundLines(border: Pair<Int, Int>, colour: String) {
        val backgroundElement = document.createElementNS(SVG_NAMESPACE_URI, "rect").also {
            with(it) {
                setAttribute("x", "$xMinCoordinate")
                setAttribute("y", "${border.first}")
                setAttribute("width", "${xMaxCoordinate - xMinCoordinate}")
                setAttribute("height", "${border.first - border.second}")
                setAttribute("fill", colour)
            }
        }
        svgElement.appendChild(backgroundElement)
    }


    companion object {
        internal const val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"

        private fun formatPitchFrequence(pitch: Float): String {
            return pitch.asDynamic().toFixed(2) as String
        }

    }


}