package com.kjipo.svg

import com.kjipo.font.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory


val verticalNoteSpacing = 12


fun writeToFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val documentFactory = DocumentBuilderFactory.newInstance()
    val document = documentFactory.newDocumentBuilder().domImplementation.createDocument(SvgTools.SVG_NAMESPACE_URI, "svg", null)
    val rootElement = document.documentElement

    var xStart = 100
    val yStart = 400

    // TODO Draw sequence
    drawBarLines(rootElement, xStart, yStart)

    for (i in 0..renderingSequence.renderingElements.size - 1) {
        val renderingElement = renderingSequence.renderingElements.get(i)
        val point = renderingSequence.points.get(i)

        for (pathInterface in renderingElement.renderingPath) {
            drawGlyph(xStart + point.x, yStart + point.y, pathInterface, rootElement)
        }
    }

    SvgTools.writeDocumentToFile(document, outputFilePath)
}


fun drawBarLines(element: Element, xStart: Int, gLine: Int) {
    val width = 500
    val spaceBetweenLines = 2 * verticalNoteSpacing

    var x = xStart
    var y = gLine - spaceBetweenLines * 3

    drawLine(x, y, x, y + 4 * spaceBetweenLines, element, 1)
    drawLine(x + width, y, x + width, y + 4 * spaceBetweenLines, element, 1)
    for (i in 0..4) {
        drawLine(x, y, x + width, y, element, 1)
        y += spaceBetweenLines
    }
}

fun drawGlyph(x: Int, y: Int, pathInterface: PathInterface, node: Node) {
    SvgTools.addPath(node, transformToPathString(translateGlyph(pathInterface, x, y)), pathInterface.strokeWidth)
}

fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node, strokeWidth: Int) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node, strokeWidth)
}

fun addStem(boundingBox: BoundingBox, stemUp: Boolean = true): PathInterface {
    val yEnd = if (stemUp) -100.0 else 100.0
    return translateGlyph(PathInterfaceImpl(listOf(PathElement(PathCommand.MOVE_TO_ABSOLUTE, listOf(0.0, 0.0)),
            PathElement(PathCommand.VERTICAL_LINE_TO_RELATIVE, listOf(0.0, yEnd))), 3), boundingBox.xMax.toInt(), 0)
}


fun main(args: Array<String>) {
    val x = 0
    val y = 0

    val noteHeadS2 = GlyphFactory.getGlyph("noteheads.s2")
    val noteHeadS1 = GlyphFactory.getGlyph("noteheads.s1")
    val noteHeadS0 = GlyphFactory.getGlyph("noteheads.s0")

    val point1 = Point(x, y)
    val point2 = Point(x + 50, y)
    val point3 = Point(x + 150, y)
    val point4 = Point(x + 200, y)

    val temporalElementSequence = RenderingSequence(listOf(
            RenderingElement(listOf(GlyphFactory.getGlyph("clefs.G"))),
            RenderingElement(listOf(noteHeadS2, addStem(noteHeadS2.boundingBox))),
            RenderingElement(listOf(noteHeadS1, addStem(noteHeadS1.boundingBox))),
            RenderingElement(listOf(noteHeadS0, addStem(noteHeadS0.boundingBox)))),
            listOf(point1, point2, point3, point4))


    val chord = createChord(listOf(Note(60, NoteType.QUARTER_NOTE), Note(62, NoteType.HALF_NOTE)))
    val chord2 = createChord(listOf(Note(62, NoteType.QUARTER_NOTE), Note(64, NoteType.QUARTER_NOTE)))
    val temporalElementSequence2 = RenderingSequence(listOf(chord, chord2,
            RenderingElement(RenderingElement(listOf(noteHeadS2, addStem(noteHeadS2.boundingBox))),
                    addAdditionalBarLines(Note(56, NoteType.HALF_NOTE)))),
            listOf(point1, point2, point3))

    val path = Paths.get("/home/student/test_output.xml")

//    writeToFile(temporalElementSequence, path)

    writeToFile(temporalElementSequence2, path)

}