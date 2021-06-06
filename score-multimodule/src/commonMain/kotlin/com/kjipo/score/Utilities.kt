package com.kjipo.score

import kotlin.math.abs
import kotlin.math.roundToInt


internal fun determineViewBox(renderingElements: Collection<PositionedRenderingElementParent>): ViewBox {
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

    return ViewBox(
        xMin.roundToInt(),
        yMin.roundToInt(),
        xMax.roundToInt(),
        yMax.roundToInt()
    )
}


/**
 * It is not necessary to include too many decimal places in the JSON output. This method is a quick fix for truncating the number of decimals in the output.
 */
internal fun truncateNumbers(scoreAsJsonString: String, decimalPlacesToInclude: Int = 4): String {
    val regexp = Regex("-?(?:0|[1-9]\\d*)(?:\\.\\d+)?(?:[eE][+-]?\\d+)?")
    val matchResults = regexp.findAll(scoreAsJsonString)

    val numbersToReplace = matchResults.map {
        val indexOfSeparator = it.value.indexOf('.')

        if (indexOfSeparator == -1) {
            null
        } else {
            val decimalPoints = it.value.substring(indexOfSeparator + 1)
            if (decimalPoints.length > decimalPlacesToInclude) {
                var numberAsString = it.value.substring(0, indexOfSeparator + decimalPlacesToInclude + 1)
                // Remove unnecessary trailing 0 after the decimal point
                while (numberAsString.endsWith('0')) {
                    numberAsString = numberAsString.substring(0, numberAsString.length - 1)
                }
                if (numberAsString.endsWith('.')) {
                    numberAsString = numberAsString.substring(0, numberAsString.length - 1)
                }
                if (numberAsString == "-0") {
                    numberAsString = "0"
                }
                Pair(it.value, numberAsString)
            } else {
                null
            }
        }
    }.filterNotNull().toList()

    var scoreWithShortenedNumbers = scoreAsJsonString
    for (numberToReplace in numbersToReplace) {
        scoreWithShortenedNumbers = scoreWithShortenedNumbers.replace(numberToReplace.first, numberToReplace.second)
    }

    return scoreWithShortenedNumbers
}
