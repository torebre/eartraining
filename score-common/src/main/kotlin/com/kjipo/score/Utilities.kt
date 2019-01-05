package com.kjipo.score

import kotlin.math.abs
import kotlin.math.roundToInt


internal fun determineViewBox(renderingElements: Collection<PositionedRenderingElement>): ViewBox {
    var xMin = 0.0
    var yMin = 0.0
    var xMax = 0.0
    var yMax = 0.0
    renderingElements.forEach {
        if (xMin > it.boundingBox.xMin) {
            xMin = it.boundingBox.xMin
        }
        if (xMax < it.boundingBox.xMax) {
            xMax = it.boundingBox.xMax
        }
        if (yMin > it.boundingBox.yMin) {
            yMin = it.boundingBox.yMin
        }
        if (yMax < it.boundingBox.yMax) {
            yMax = it.boundingBox.yMax
        }
    }

    xMin -= LEFT_MARGIN
    yMin -= TOP_MARGIN
    xMax += RIGHT_MARGIN
    yMax += BOTTOM_MARGIN

    // This is to try to get to cropping when xMin and/or yMin are less than 0
    if (xMin < 0) {
        xMax += abs(xMin)
    }
    if (yMin < 0) {
        yMax += abs(yMin)
    }

    return ViewBox(xMin.roundToInt(),
            yMin.roundToInt(),
            xMax.roundToInt(),
            yMax.roundToInt())
}