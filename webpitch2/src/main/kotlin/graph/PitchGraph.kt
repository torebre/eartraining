package graph

import kotlinx.browser.document
import mu.KotlinLogging
import org.w3c.dom.svg.SVGElement
import kotlin.math.log2

class PitchGraph(svgElementId: String, pitchGraphModel: PitchGraphModel) : PitchGraphModelListener {
    private val xMinCoordinate = 0
    private val yMinCoordinate = 0
    private val xMaxCoordinate = 500
    private val yMaxCoordinate = 500

    private val yMinValue = log2(PITCH_CLASS_FREQUENCIES.first())
    private val yMaxValue = log2(PITCH_CLASS_FREQUENCIES.last())

    private val svgElement: SVGElement

    private var startTime: Long = 0
    private var lastTimePoint: Long = 0

    private var previousXCoord: Int = -1
    private var previousYCoord: Int = -1

    private var zoomLevel = 100000
    private var stepSize = zoomLevel / xMaxCoordinate
    private var pixelStepSize = 1

    private var axisRightXStart = xMaxCoordinate - 100

    private val logger = KotlinLogging.logger {}


    init {
        svgElement = document.getElementById(svgElementId) as SVGElement
        svgElement.setAttribute("viewBox", "$xMinCoordinate $yMinCoordinate $xMaxCoordinate $yMaxCoordinate")
        drawPitchAxis()

        pitchGraphModel.addPitchDataListener(this)
    }


    private fun transformToX(timestamp: Long): Int {
        return (pixelStepSize * (lastTimePoint / stepSize)).toInt()
    }


    private fun transformToY(pitch: Float): Int {
        val pitchOnLogScale = log2(pitch)
        val yCoord = yMaxCoordinate - ((yMaxValue - pitchOnLogScale) / (yMaxValue - yMinValue)) * (yMaxCoordinate - yMinCoordinate)
        return turnYAxis(yCoord.toInt())
    }

    override fun newPitchDataReceived(pitchData: PitchDataWithTime) {
        val xCoord = transformToX(pitchData.timeStamp)
        val yCoord = transformToY(pitchData.pitch)

        if (startTime == 0L) {
            startTime = pitchData.timeStamp
        }

        lastTimePoint = pitchData.timeStamp - startTime

        if (previousXCoord != -1) {
            connectPointsWithLine(xCoord, yCoord)
        }

        addPointToGraph(xCoord, yCoord)

        previousXCoord = xCoord
        previousYCoord = yCoord
    }

    private fun turnYAxis(yCoord: Int) = yMaxCoordinate - yCoord

    override fun uncertainPitchReceived(timestamp: Long) {
        // TODO Mark in graph

        // Set to -1 to avoid line being drawn from uncertain pitch to certain
        previousXCoord = -1
    }


    private fun addPointToGraph(xCoord: Int, yCoord: Int) {
        val newPoint = document.createElementNS(SVG_NAMESPACE_URI, "circle").also {
            with(it) {
                setAttribute("cx", "$xCoord")
                setAttribute("cy", "$yCoord")
                setAttribute("r", "2")
                setAttribute("fill", "red")
            }
        }

        svgElement.appendChild(newPoint)
    }

    private fun connectPointsWithLine(xCoord: Int, yCoord: Int) {
        val newLineSegment = document.createElementNS(SVG_NAMESPACE_URI, "line").also {
            with(it) {
                setAttribute("x1", "$previousXCoord")
                setAttribute("y1", "$previousYCoord")
                setAttribute("x2", "$xCoord")
                setAttribute("y2", "$yCoord")
                setAttribute("stroke", "black")
            }
        }
        svgElement.appendChild(newLineSegment)
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