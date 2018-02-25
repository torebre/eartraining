package com.kjipo.font


private val STEP_SIZE = 0.1

data class CoordinatePair(val x: Double, val y: Double, val skipLine: Boolean = false)

fun processPath(pathElements: List<PathElement>): List<CoordinatePair> {
    val pathAsLineSegments = mutableListOf<CoordinatePair>()

    var previousPathElement: PathElement? = null

    for (pathElement in pathElements) {
        when (pathElement.command) {
            PathCommand.CURVE_TO_RELATIVE -> processCurveToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.VERTICAL_LINE_TO_RELATIVE -> processVerticalLineToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.VERTICAL_LINE_TO_ABSOLUTE -> processVerticalLineToAbsolute(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.HORIZONAL_LINE_TO_RELATIVE -> processHorizontalLineToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.MOVE_TO_ABSOLUTE -> processMoveToAbsolute(pathElement.numbers)
            PathCommand.MOVE_TO_RELATIVE -> processMoveToRelative(pathElement.numbers, pathAsLineSegments.lastOrNull())
            PathCommand.LINE_TO_RELATIVE -> processLineToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.CLOSE_PATH -> emptyList()
            PathCommand.SMOOTH_CURVE_TO_RELATIVE -> processSmoothCurveToRelative(pathElement.numbers, pathAsLineSegments.lastOrNull(), previousPathElement)
        }.let { pathAsLineSegments.addAll(it) }
        previousPathElement = pathElement
    }

    return pathAsLineSegments;
}

fun processVerticalLineToAbsolute(numbers: List<Double>, startCoordinate: CoordinatePair): List<CoordinatePair> {
    val coordinatePairs = mutableListOf<CoordinatePair>()
    val currentY = numbers.get(0)

    coordinatePairs.add(CoordinatePair(startCoordinate.x, currentY, true))

    return coordinatePairs
}


private fun processMoveToRelative(numbers: List<Double>, startCoordinate: CoordinatePair?): List<CoordinatePair> {
    val coordinatePairs = mutableListOf<CoordinatePair>()

    if (startCoordinate == null) {
        coordinatePairs.add(CoordinatePair(numbers.get(0), numbers.get(1), true))
    } else {
        coordinatePairs.add(CoordinatePair(startCoordinate.x + numbers.get(0), startCoordinate.y + numbers.get(1), true))
    }

    for (i in 2..numbers.size - 1 step 2) {
        coordinatePairs.add(CoordinatePair(numbers.get(i) + coordinatePairs.last().x, numbers.get(i + 1) + coordinatePairs.last().y))
    }

    return coordinatePairs
}

private fun processVerticalLineToRelative(numbers: List<Double>, startCoordinate: CoordinatePair): List<CoordinatePair> {
    var currentY = startCoordinate.y

    val newCoordinates = mutableListOf<CoordinatePair>()

    for (i in 0..numbers.size - 1) {
        currentY += numbers.get(i)
        newCoordinates.add(CoordinatePair(startCoordinate.x, currentY))
    }
    return newCoordinates
}

private fun processHorizontalLineToRelative(numbers: List<Double>, startCoordinate: CoordinatePair): List<CoordinatePair> {
    var currentX = startCoordinate.x

    val newCoordinates = mutableListOf<CoordinatePair>()

    for (i in 0..numbers.size - 1) {
        currentX += numbers.get(i)
        newCoordinates.add(CoordinatePair(currentX, startCoordinate.y))
    }
    return newCoordinates
}

private fun processMoveToAbsolute(numbers: List<Double>): List<CoordinatePair> {
    val coordinatePairs = mutableListOf<CoordinatePair>()
    var currentX = numbers.get(0)
    var currentY = numbers.get(1)

    coordinatePairs.add(CoordinatePair(currentX, currentY, true))

    for (i in 2..numbers.size - 1 step 2) {
        currentX += numbers.get(i)
        currentY += numbers.get(i + 1)

        coordinatePairs.add(CoordinatePair(currentX, currentY))
    }

    return coordinatePairs
}

private fun processLineToRelative(numbers: List<Double>, startCoordinate: CoordinatePair): List<CoordinatePair> {
    val coordinatePairs = mutableListOf<CoordinatePair>()
    var currentX = startCoordinate.x
    var currentY = startCoordinate.y

    for (i in 0..numbers.size - 1 step 2) {
        currentX += numbers.get(i)
        currentY += numbers.get(i + 1)
        coordinatePairs.add(CoordinatePair(currentX, currentY))
    }

    return coordinatePairs
}

private fun processSmoothCurveToRelative(numbers: List<Double>, startCoordinate: CoordinatePair?,
                                         previousPathElement: PathElement?): List<CoordinatePair> {
    var filteredStartCoordinate: CoordinatePair

    if (startCoordinate != null) {
        filteredStartCoordinate = startCoordinate
    } else {
        filteredStartCoordinate = CoordinatePair(0.0, 0.0)
    }

    var firstControlPoint: CoordinatePair
    if (previousPathElement != null) {
        if (setOf<PathCommand>(PathCommand.CURVE_TO_RELATIVE, PathCommand.SMOOTH_CURVE_TO_RELATIVE).contains(previousPathElement.command)) {
            firstControlPoint = CoordinatePair(2 * filteredStartCoordinate.x - previousPathElement.numbers.get(previousPathElement.numbers.size - 4),
                    2 * filteredStartCoordinate.y - previousPathElement.numbers.get(previousPathElement.numbers.size - 3))
        } else {
            firstControlPoint = filteredStartCoordinate
        }


    } else {
        firstControlPoint = filteredStartCoordinate
    }

    val coordinatePairs = mutableListOf<CoordinatePair>()
//    coordinatePairs.add(filteredStartCoordinate)
//    coordinatePairs.add(firstControlPoint)

    for (i in 0..numbers.size - 1 step 2) {
        coordinatePairs.add(CoordinatePair(numbers.get(i) + filteredStartCoordinate.x, numbers.get(i + 1) + filteredStartCoordinate.y))
    }

    val n = coordinatePairs.size
    val newCoordinates = mutableListOf<CoordinatePair>()
    for (curveNumber in 0..n - 1 step 2) {
        val bezierCurve = processSingleCubicBezierCurve(listOf(filteredStartCoordinate, firstControlPoint,
                coordinatePairs.get(curveNumber), coordinatePairs.get(curveNumber + 1)))
        filteredStartCoordinate = bezierCurve.last()
        firstControlPoint = coordinatePairs.get(curveNumber)
        newCoordinates.addAll(bezierCurve)
    }
    return newCoordinates.map { coordinatePair -> CoordinatePair(coordinatePair.x + filteredStartCoordinate.x, coordinatePair.y + filteredStartCoordinate.y) }
}

fun processCurveToRelative(numbers: List<Double>, startCoordinate: CoordinatePair): List<CoordinatePair> {
    val inputPoints = mutableListOf<CoordinatePair>()

    // TODO This will cause duplicate points
//    inputPoints.add(startCoordinate)

    for (i in 0..numbers.size - 1 step 2) {
        inputPoints.add(CoordinatePair(numbers.get(i) + startCoordinate.x, numbers.get(i + 1) + startCoordinate.y))
    }

    val n = inputPoints.size
    var currentPoint = startCoordinate
    val outputPoints = mutableListOf<CoordinatePair>()

    for (curveNumber in 0..n - 1 step 3) {
        val bezierCurve = processSingleCubicBezierCurve(listOf(currentPoint, inputPoints.get(curveNumber),
                inputPoints.get(curveNumber + 1), inputPoints.get(curveNumber + 2)))
        outputPoints.addAll(bezierCurve)
        currentPoint = inputPoints.get(curveNumber + 2)
    }
    return outputPoints
}

fun processSingleCubicBezierCurve(points: List<CoordinatePair>): List<CoordinatePair> {
    val xCoordinates = mutableListOf<Double>()
    val yCoordinates = mutableListOf<Double>()

    for (t in generateSequence({ 0.0 }, { it + STEP_SIZE }).takeWhile { it < 1.0 }) {
        xCoordinates.add(evaluateCubicBezierCurvePolynomial(t, points.get(0).x, points.get(1).x, points.get(2).x, points.get(3).x))
        yCoordinates.add(evaluateCubicBezierCurvePolynomial(t, points.get(0).y, points.get(1).y, points.get(2).y, points.get(3).y))
    }

    xCoordinates.add(points.get(3).x)
    yCoordinates.add(points.get(3).y)

    return (0..xCoordinates.size - 1).map { CoordinatePair(xCoordinates.get(it), yCoordinates.get(it)) }.toList()
}

private fun evaluateCubicBezierCurvePolynomial(t: Double, p0: Double, p1: Double, p2: Double, p3: Double): Double {
    return Math.pow(1 - t, 3.0) * p0 + 3 * Math.pow(1 - t, 2.0) * p1 * t + 3 * (1 - t) * p2 * Math.pow(t, 2.0) + Math.pow(t, 3.0) * p3
}

data class BoundingBox(val xMin: Double, val yMin: Double, val xMax: Double, val yMax: Double)

fun BoundingBox.height(): Double {
    return Math.abs(yMax.minus(yMin))
}

fun BoundingBox.width(): Double {
    return Math.abs(xMax.minus(xMin))
}

private fun findBoundingBoxInternal(coordinates: Iterable<CoordinatePair>): BoundingBox {
    return BoundingBox(coordinates.map { coordinatePair -> coordinatePair.x }.min() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.y }.min() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.x }.max() ?: 0.0,
            coordinates.map { coordinatePair -> coordinatePair.y }.max() ?: 0.0)
}

fun findBoundingBox(pathElements: List<PathElement>): BoundingBox {
    return findBoundingBoxInternal(processPath(pathElements))
}

fun offSetBoundingBox(boundingBox: BoundingBox, xOffset: Int, yOffset: Int): BoundingBox {
    return BoundingBox(boundingBox.xMin + xOffset, boundingBox.yMin + yOffset, boundingBox.xMax + xOffset, boundingBox.yMax + yOffset)
}


fun invertYCoordinates(glyphData: GlyphData): GlyphData {
    val newFontPathElements = mutableListOf<PathElement>()
    for (fontPathElement in glyphData.pathElements) {
        when (fontPathElement.command) {
            PathCommand.VERTICAL_LINE_TO_RELATIVE -> fontPathElement.numbers.map { -it }
            PathCommand.VERTICAL_LINE_TO_ABSOLUTE -> invertEverySecondNumber(fontPathElement.numbers)
            PathCommand.HORIZONAL_LINE_TO_RELATIVE -> fontPathElement.numbers
            PathCommand.MOVE_TO_ABSOLUTE -> invertEverySecondNumber(fontPathElement.numbers)
            PathCommand.MOVE_TO_RELATIVE -> invertEverySecondNumber(fontPathElement.numbers)
            PathCommand.LINE_TO_RELATIVE -> invertEverySecondNumber(fontPathElement.numbers)
            PathCommand.CURVE_TO_RELATIVE -> invertEverySecondNumber(fontPathElement.numbers)
            PathCommand.CLOSE_PATH -> emptyList()
            PathCommand.SMOOTH_CURVE_TO_RELATIVE -> invertEverySecondNumber(fontPathElement.numbers)
        }.let { newFontPathElements.add(PathElement(fontPathElement.command, it)) }

    }
    return GlyphData(glyphData.name, newFontPathElements, findBoundingBox(newFontPathElements))
}


fun invertEverySecondNumber(numbers: Iterable<Double>): List<Double> {
    var invert = true

    return numbers.map {
        invert = !invert
        if (invert) {
            -it
        } else {
            it
        }
    }.toList()
}


fun scaleGlyph(glyphData: GlyphData, scaleFactor: Double): GlyphData {
    val newPathElements = glyphData.pathElements.map { it -> PathElement(it.command, it.numbers.map { it * scaleFactor }.toList()) }
    return GlyphData(glyphData.name, newPathElements, findBoundingBox(newPathElements))
}


fun translateGlyph(pathInterface: PathInterface, xTranslate: Int, yTranslate: Int): PathInterface {
    return PathInterfaceImpl(pathInterface.pathElements.map { translateFontPathElement(it, xTranslate, yTranslate) }, pathInterface.strokeWidth)
}

fun translateFontPathElement(pathElement: PathElement, xTranslate: Int, yTranslate: Int): PathElement {
    return when (pathElement.command) {
        PathCommand.MOVE_TO_ABSOLUTE -> PathElement(pathElement.command, translateAbsoluteMovement(pathElement.numbers, xTranslate, yTranslate))
        else -> pathElement
    }
}

fun translateAbsoluteMovement(numbers: List<Double>, xTranslate: Int, yTranslate: Int): List<Double> {
    var isYCoordinate = true

    return numbers.map {
        isYCoordinate = !isYCoordinate
        if (isYCoordinate) {
            it + yTranslate
        } else {
            it + xTranslate
        }
    }

}

fun transformToPathString(pathInterface: PathInterface) = transformToPathString(pathInterface.pathElements)


fun transformToPathString(pathElements: Collection<PathElement>): String {
    return pathElements.map {
        it.command.command
                .plus(" ")
                .plus(it.numbers.joinToString(" ") {
                    ReadFonts.decimalFormatThreadLocal.get().format(it)
                })
    }
            .joinToString(" ")

}