package com.kjipo.font

import com.kjipo.svg.CoordinatePair
import com.kjipo.svg.processPath
import javafx.scene.paint.Color
import org.slf4j.LoggerFactory
import tornadofx.*


class FontPathVisualizer : View("Glyph view") {

    private val LOGGER = LoggerFactory.getLogger(FontPathVisualizer::class.java)

    override val root = stackpane {
        val lineSegments = processPath(FontProcessingUtilities.parsePathData("M169.16 177.877c-98.7212 -4.927 -169.16 -88.5371 -169.16 -178.835c0 -26.0341 5.85524 -52.6241 18.576 -78.0445c32.8807 -65.6609 96.559 -99.4419 160.457 -99.4419c56.415 0 113.001 26.332 148.717 80.3051c20.076 30.3394 29.359 63.977 29.359 97.0512 c0 78.0509 -51.7 152.964 -135.275 173.991c-10.89 2.733 -32.865 5.541 -42.039 5.541c-1.161 0 -2.117 -0.0449982 -2.819 -0.141006c-0.237 0 -3.789 -0.237 -7.81599 -0.425995zM188.581 131.74c21.079 -1.23201 44.574 -9.23701 62.479 -21.221l3.506 -2.321 l-37.99 -37.9896l-37.942 -37.9421l-37.942 37.9421l-37.99 37.9896l3.506 2.321c18.663 12.505 41.731 20.084 65.084 21.458c2.502 0.138 4.30699 0.209991 6.23099 0.209991c2.701 0 5.63501 -0.14299 11.058 -0.446991zM108.244 -38.1231l-37.989 -37.9895 l-2.3211 3.5053c-8.1 12.1263 -14.4947 26.9052 -18.0473 41.8736c-2.3734 9.9496 -3.536 20.1573 -3.536 30.348c0 25.8086 7.4563 51.5083 21.5833 72.631l2.3211 3.5052l37.989 -37.9894l37.943 -37.9421zM289.381 72.1979 c14.323 -21.4253 21.488 -46.8974 21.488 -72.371c0 -25.4947 -7.177 -50.9911 -2A1.535 -72.4341l-2.32101 -3.5053l-37.99 37.9895l-37.942 37.9421l37.895 37.8947c20.842 20.8421 37.942 37.8947 37.99 37.8947c0.0469971 0 1.13599 -1.5631 2.41501 -3.4105z M216.718 -70.7126l37.848 -37.8474l-3.506 -2.321c-12.126 -8.10001 -26.905 -14.495 -41.873 -18.047c-9.95 -2.37401 -20.158 -3.53601 -30.348 -3.53601c-25.809 0 -51.509 7.456 -72.631 21.583l-3.506 2.321l37.848 37.8474 c20.842 20.8421 37.942 37.8474 38.084 37.8474s17.242 -17.0053 38.084 -37.8474z"))

        group {
            var previousPoint: CoordinatePair? = null

            for (lineSegment in lineSegments) {

                if (previousPoint != null && !lineSegment.skipLine) {

                    line {
                        startX = previousPoint?.x ?: 0.0
                        startY = previousPoint?.y ?: 0.0
                        endX = lineSegment.x
                        endY = lineSegment.y

                        stroke = Color.BLUE
                        strokeWidth = 1.0
                    }
                }
                previousPoint = lineSegment

            }
        }
    }

}
