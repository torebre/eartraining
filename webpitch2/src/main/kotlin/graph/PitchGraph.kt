package graph

import kotlinx.browser.document
import org.w3c.dom.svg.SVGElement

class PitchGraph(svgElementId: String, pitchGraphModel: PitchGraphModel) : PitchGraphModelListener {
    private val xMin = 0
    private val yMin = 0
    private val xMax = 500
    private val yMax = 500

    private val svgElement: SVGElement

    private var startTime: Long = 0
    private var lastTimePoint: Long = 0

    private var previousXCoord: Int = -1
    private var previousYCoord: Int = -1

    private var zoomLevel = 100000
    private var stepSize = zoomLevel / xMax
    private var pixelStepSize = 1


    init {
        svgElement = document.getElementById(svgElementId) as SVGElement
        svgElement.setAttribute("viewBox", "$xMin $yMin $xMax $yMax")

        pitchGraphModel.addPitchDataListener(this)
    }


    private fun transformToX(timestamp: Long): Int {
        return (pixelStepSize * (lastTimePoint / stepSize)).toInt()
    }


    private fun transformToY(pitch: Float): Int {
        // TODO For now just convert the value directly to an integer
        return pitch.toInt()
    }

    override fun newPitchDataReceived(pitchData: PitchDataWithTime) {
        val xCoord = transformToX(pitchData.timeStamp)
        val yCoord = transformToY(pitchData.pitch)

        if (startTime == 0L) {
            startTime = pitchData.timeStamp
        }

        lastTimePoint = pitchData.timeStamp - startTime

        if(previousXCoord != -1) {
            connectPointsWithLine(xCoord, yCoord)
        }

        addPointToGraph(xCoord, yCoord)

        previousXCoord = xCoord
        previousYCoord = yCoord
    }

    override fun uncertainPitchReceived(timestamp: Long) {
        // TODO Mark in graph
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


    companion object {
        internal const val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"

    }


}