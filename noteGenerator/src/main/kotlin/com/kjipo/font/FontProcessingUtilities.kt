package com.kjipo.font

import com.kjipo.svg.PathCommand
import com.kjipo.svg.PathElement
import org.apache.commons.lang3.CharUtils
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

object FontProcessingUtilities {
    private val LOGGER = LoggerFactory.getLogger(FontProcessingUtilities::class.java)


    @Throws(IOException::class)
    fun parsePathData(pathData: String): List<PathElement> {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Processing {}", pathData)
        }

        val charStream = ByteArrayInputStream(pathData.toByteArray())
        val pathElements = ArrayList<PathElement>()

        var c = charStream.read()
        var parsedElement: Map.Entry<Int, PathElement>
        val stringBuilder = StringBuilder()

        while (c != -1) {
            stringBuilder.append(c.toChar())

            when (c) {

                'v'.toInt() -> {
                    parsedElement = handleLowercaseV(charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

            //                case 'H':
            //                    absolute = true;
                'h'.toInt() -> {
                    parsedElement = handleLowerCaseH(charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

                'M'.toInt() -> {
                    parsedElement = handleGeneric(PathCommand.MOVE_TO_ABSOLUTE, charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

                'm'.toInt() -> {
                    parsedElement = handleGeneric(PathCommand.MOVE_TO_RELATIVE, charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

                'l'.toInt() -> {
                    parsedElement = handleLowerCaseL(charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

                'c'.toInt() -> {
                    parsedElement = handleLowerCaseC(charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

                'Z'.toInt(), 'z'.toInt() -> {
                    pathElements.add(PathElement(PathCommand.CLOSE_PATH, emptyList()))
                    c = charStream.read()
                }

                's'.toInt() -> {
                    parsedElement = handleGeneric(PathCommand.SMOOTH_CURVE_TO_RELATIVE, charStream).entries.iterator().next()
                    c = parsedElement.key
                    pathElements.add(parsedElement.value)
                }

                ' '.toInt() ->
                    // Blank space between commands
                    c = charStream.read()

                else -> {

                    LOGGER.error("Hit default: {}. Characters read: {}", c.toChar(), stringBuilder)

                    //                    stringBuilder.append((char) c).append(" ");
                    c = charStream.read()
                }
            }

        }

        return pathElements
    }


    @Throws(IOException::class)
    private fun handleLowerCaseL(charStream: InputStream): Map<Int, PathElement> {
        return handleGeneric(PathCommand.LINE_TO_RELATIVE, charStream)
    }

    @Throws(IOException::class)
    private fun handleGeneric(pathCommand: PathCommand, charStream: InputStream): Map<Int, PathElement> {
        var c: Int
        val number = ArrayList<Char>()
        val numbers = ArrayList<Double>()

        do {
            c = readNumber(charStream, number)
            if (number.isEmpty()) {
                break
            }

            numbers.add(parseNumber(number))

            number.clear()
            c = readNumber(charStream, number)
            if (number.isEmpty()) {
                break
            }
            numbers.add(parseNumber(number))
            number.clear()
        } while (!CharUtils.isAsciiAlpha(c.toChar()))

        return Collections.singletonMap(c, PathElement(pathCommand, numbers))
    }


    @Throws(IOException::class)
    private fun handleLowercaseV(charStream: InputStream): Map<Int, PathElement> {
        var c: Int
        val number = ArrayList<Char>()

        val numbers = ArrayList<Double>()
        do {
            c = readNumber(charStream, number)
            if (number.isEmpty()) {
                break
            }
            numbers.add(parseNumber(number))
            number.clear()

        } while (!CharUtils.isAsciiAlpha(c.toChar()))

        return Collections.singletonMap(c, PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, numbers))

    }

    @Throws(IOException::class)
    private fun handleLowerCaseH(charStream: InputStream): Map<Int, PathElement> {
        var c: Int
        val number = ArrayList<Char>()
        val numbers = ArrayList<Double>()

        do {
            c = readNumber(charStream, number)
            if (number.isEmpty()) {
                break
            }
            numbers.add(parseNumber(number))
            number.clear()
        } while (!CharUtils.isAsciiAlpha(c.toChar()))

        return Collections.singletonMap(c, PathElement(PathCommand.HORIZONAL_LINE_TO_RELATIVE, numbers))
    }


    @Throws(IOException::class)
    private fun handleLowerCaseC(charStream: InputStream): Map<Int, PathElement> {
        var c = -1
        val number = ArrayList<Char>()
        val numbers = ArrayList<Double>()

        do {
            var pair = Pair(-1, false)
            for (i in 0..5) {
                pair = processNumber(charStream, number, numbers)
                c = pair.first
                if (pair.second) {
                    break
                }
            }
            if (pair.second) {
                break
            }
        } while (!CharUtils.isAsciiAlpha(c.toChar()))

        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Numbers: {}", numbers)
        }

        return Collections.singletonMap(c, PathElement(PathCommand.CURVE_TO_RELATIVE, numbers))
    }

    @Throws(IOException::class)
    private fun processNumber(charStream: InputStream, number: MutableList<Char>, numbers: MutableList<Double>): Pair<Int, Boolean> {
        val c = readNumber(charStream, number)
        if (number.isEmpty()) {
            return Pair(c, true)
        }
        numbers.add(parseNumber(number))
        number.clear()
        return Pair(c, false)
    }


    private fun parseNumber(number: List<Char>): Double {
        val temp = CharArray(number.size)
        var i = 0
        for (character in number) {
            temp[i++] = character
        }
        return java.lang.Double.valueOf(String(temp))
    }

    @Throws(IOException::class)
    private fun readNumber(input: InputStream, number: MutableList<Char>): Int {
        var c = input.read().toChar()
        while (c == ' ') {
            c = input.read().toChar()
        }
        while (CharUtils.isAsciiNumeric(c) ||
                c == 'e' ||
                c == '-' ||
                c == '.') {
            number.add(c)
            c = input.read().toChar()
        }
        return c.toInt()
    }

}
