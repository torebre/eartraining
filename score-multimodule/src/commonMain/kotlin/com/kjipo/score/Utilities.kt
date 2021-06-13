package com.kjipo.score

import com.kjipo.svg.BoundingBox
import mu.KotlinLogging
import kotlin.math.abs
import kotlin.math.roundToInt


private val logger = KotlinLogging.logger {}


private data class MutableViewBox(
    var xMin: Double = Double.NaN,
    var yMin: Double = Double.NaN,
    var xMax: Double = Double.NaN,
    var yMax: Double = Double.NaN
) {

    fun updateWithBoundingBox(boundingBox: BoundingBox) {
        if (xMin.isNaN() || xMin > boundingBox.xMin) {
            xMin = boundingBox.xMin
        }
        if (xMax.isNaN() || xMax < boundingBox.xMax) {
            xMax = boundingBox.xMax
        }
        if (yMin.isNaN() || yMin > boundingBox.yMin) {
            yMin = boundingBox.yMin
        }
        if (yMax.isNaN() || yMax < boundingBox.yMax) {
            yMax = boundingBox.yMax
        }
    }

}


internal fun determineDebugBox(id: String, renderingElements: Collection<PositionedRenderingElementParent>): Box {
    MutableViewBox().also { mutableViewBox ->
        renderingElements.forEach { positionedRenderingElementParent ->
            val boundingBox = calculateBoundingBoxInAbsoluteCoordinates(positionedRenderingElementParent)
            mutableViewBox.updateWithBoundingBox(boundingBox)
        }
    }.run {
        return Box(xMin.toInt(), yMin.toInt(), abs(xMax - xMin).toInt(), abs(yMax - yMin).toInt(), id)
    }
}

private fun calculateBoundingBoxInAbsoluteCoordinates(positionedRenderingElementParent: PositionedRenderingElementParent): BoundingBox {
    return when (positionedRenderingElementParent) {
        is AbsolutelyPositionedRenderingElement -> {
            positionedRenderingElementParent.boundingBox
        }
        is TranslatedRenderingElement -> {
            positionedRenderingElementParent.boundingBox.let {
                BoundingBox(
                    it.xMin + positionedRenderingElementParent.translation.xShift,
                    it.yMin + positionedRenderingElementParent.translation.yShift,
                    it.xMax + positionedRenderingElementParent.translation.xShift,
                    it.yMax + positionedRenderingElementParent.translation.yShift
                )
            }
        }
        is TranslatedRenderingElementUsingReference -> {
            // TODO Try to remove the bounding box from TranslatedRenderingElementUsingReference
            positionedRenderingElementParent.boundingBox.let {
                BoundingBox(
                    it.xMin + positionedRenderingElementParent.translation.xShift,
                    it.yMin + positionedRenderingElementParent.translation.yShift,
                    it.xMax + positionedRenderingElementParent.translation.xShift,
                    it.yMax + positionedRenderingElementParent.translation.yShift
                )
            }
        }
    }
}

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

internal fun getNoteWithoutAccidental(noteType: NoteType): GClefNoteLine {
    return when (noteType) {
        NoteType.A_SHARP -> GClefNoteLine.A
        NoteType.A -> GClefNoteLine.A
        NoteType.H -> GClefNoteLine.H
        NoteType.C -> GClefNoteLine.C
        NoteType.C_SHARP -> GClefNoteLine.C
        NoteType.D -> GClefNoteLine.D
        NoteType.D_SHARP -> GClefNoteLine.D
        NoteType.E -> GClefNoteLine.E
        NoteType.F -> GClefNoteLine.F
        NoteType.F_SHARP -> GClefNoteLine.F
        NoteType.G -> GClefNoteLine.G
        NoteType.G_SHARP -> GClefNoteLine.G
    }
}


internal fun noteRequiresSharp(noteType: NoteType): Boolean {
    return when (noteType) {
        NoteType.A_SHARP, NoteType.C_SHARP, NoteType.D_SHARP, NoteType.F_SHARP, NoteType.G_SHARP -> true
        else -> false
    }
}

