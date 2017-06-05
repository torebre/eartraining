package com.kjipo.font


private val STEP_SIZE = 0.1

data class CoordinatePair(val x: Double, val y: Double, val skipLine: Boolean = false)

fun processPath(pathElements: List<FontPathElement>): List<CoordinatePair> {
    val pathAsLineSegments = mutableListOf<CoordinatePair>()

    var previousPathElement: FontPathElement? = null

    for (pathElement in pathElements) {

        println("Command: " + pathElement.command)

        when (pathElement.command) {
            PathCommand.CURVE_TO_RELATIVE -> processCurveToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.VERTICAL_LINE_TO_RELATIVE -> processVerticalLineToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.HORIZONAL_LINE_TO_RELATIVE -> processHorizontalLineToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.MOVE_TO_ABSOLUTE -> processMoveToAbsolute(pathElement.numbers)
            PathCommand.MOVE_TO_RELATIVE -> processMoveToRelative(pathElement.numbers, pathAsLineSegments.lastOrNull())
            PathCommand.LINE_TO_RELATIVE -> processLineToRelative(pathElement.numbers, pathAsLineSegments.last())
            PathCommand.CLOSE_PATH -> emptyList<CoordinatePair>()
            PathCommand.SMOOTH_CURVE_TO_RELATIVE -> processSmoothCurveToRelative(pathElement.numbers, pathAsLineSegments.lastOrNull(), previousPathElement)
        }.let { pathAsLineSegments.addAll(it) }
        previousPathElement = pathElement

        println("Last segment: " + pathAsLineSegments.lastOrNull())
    }

    return pathAsLineSegments;
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
                                         previousFontPathElement: FontPathElement?): List<CoordinatePair> {
    var filteredStartCoordinate: CoordinatePair

    if (startCoordinate != null) {
        filteredStartCoordinate = startCoordinate
    } else {
        filteredStartCoordinate = CoordinatePair(0.0, 0.0)
    }

    var firstControlPoint: CoordinatePair
    if (previousFontPathElement != null) {
        if (setOf<PathCommand>(PathCommand.CURVE_TO_RELATIVE, PathCommand.SMOOTH_CURVE_TO_RELATIVE).contains(previousFontPathElement.command)) {
            firstControlPoint = CoordinatePair(2 * filteredStartCoordinate.x - previousFontPathElement.numbers.get(previousFontPathElement.numbers.size - 4),
                    2 * filteredStartCoordinate.y - previousFontPathElement.numbers.get(previousFontPathElement.numbers.size - 3))
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
    for (curveNumber in 0..n step 2) {
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

    println("Start point: " + startCoordinate + ". Input points: " + inputPoints)

    for (curveNumber in 0..n - 1 step 3) {
        val bezierCurve = processSingleCubicBezierCurve(listOf(currentPoint, inputPoints.get(curveNumber),
                inputPoints.get(curveNumber + 1), inputPoints.get(curveNumber + 2)))
        outputPoints.addAll(bezierCurve)
        currentPoint = inputPoints.get(curveNumber + 2)
    }
    return outputPoints
}

fun processSingleCubicBezierCurve(points: List<CoordinatePair>): List<CoordinatePair> {
    val n = points.size

    val xCoordinates = mutableListOf<Double>()
    val yCoordinates = mutableListOf<Double>()

    println("Points: " + points)

    for (t in generateSequence({ 0.0 }, { it + STEP_SIZE }).takeWhile { it < 1.0 }) {

        println("t: " +t)

        xCoordinates.add(evaluateCubicBezierCurvePolynomial(t, points.get(0).x, points.get(1).x, points.get(2).x, points.get(3).x))
        yCoordinates.add(evaluateCubicBezierCurvePolynomial(t, points.get(0).y, points.get(1).y, points.get(2).y, points.get(3).y))

//        var sum1 = mutableListOf<Double>()
//        var sum2 = mutableListOf<Double>()
//
//        for (i in 0..n - 1) {
//            sum1.add(points.get(i).x * evaluateBernsteinBasisPolynomial(t, i + 1, n))
//            sum2.add(points.get(i).y * evaluateBernsteinBasisPolynomial(t, i + 1, n))
//        }
//
//        println("t: " + t + ". Sum1: " + sum1 + ". Sum2: " + sum2)
//
//        xCoordinates.add(sum1.sum())
//        yCoordinates.add(sum2.sum())

//        points.map { coordinatePair -> coordinatePair.x }.map { x -> x * evaluateBernsteinBasisPolynomial(t, i++, n) }.sum().let { xCoordinates.add(it) }
//        points.map { coordinatePair -> coordinatePair.y }.map { y -> y * evaluateBernsteinBasisPolynomial(t, j++, n) }.sum().let { yCoordinates.add(it) }
    }

    xCoordinates.add(points.get(3).x)
    yCoordinates.add(points.get(3).y)

//    var i = -1

    println("X coordinates size: " +xCoordinates.size + ". Y coordinates size: " +yCoordinates.size)

    return (0..xCoordinates.size - 1).map { CoordinatePair(xCoordinates.get(it), yCoordinates.get(it)) }.toList()

//    return generateSequence { ++i; CoordinatePair(xCoordinates.get(i), yCoordinates.get(i)) }.takeWhile { i < xCoordinates.size - 1 }.toList()
}

private fun evaluateCubicBezierCurvePolynomial(t: Double, p0: Double, p1: Double, p2: Double, p3: Double): Double {
    return Math.pow(1 - t, 3.0) * p0 + 3 * Math.pow(1 - t, 2.0) * p1 * t + 3 * (1 - t) * p2 * Math.pow(t, 2.0) + Math.pow(t, 3.0) * p3
}

//fun evaluateBernsteinBasisPolynomial(t: Double, i: Int, n: Int): Double {
//    return (factorial(n) / (factorial(n - i) * (factorial(i)))) * Math.pow(t, i.toDouble()) * Math.pow(1 - t, (n - i).toDouble())
//}

//fun factorial(n: Int): Int {
//    var product = 1
//    for (i in 2..n) {
//        product *= i
//    }
//    return product
//}


fun invertYcoordinates(coordinatePairs: List<CoordinatePair>): List<CoordinatePair> {
    return coordinatePairs.map { coordinatePair -> CoordinatePair(coordinatePair.x, -coordinatePair.y) }
}