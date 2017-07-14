package com.kjipo.svg

import com.kjipo.font.*
import org.w3c.dom.Node
import java.nio.file.Path
import java.nio.file.Paths
import javax.xml.parsers.DocumentBuilderFactory





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


fun drawGlyph(x: Int, y: Int, pathInterface: PathInterface, node: Node) {
    SvgTools.addPath(node, transformToPathString(translateGlyph(pathInterface, x, y)), pathInterface.strokeWidth)
}

fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node, strokeWidth: Int) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node, strokeWidth)
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
            RenderingElementImpl(listOf(GlyphFactory.getGlyph("clefs.G"))),
            RenderingElementImpl(listOf(noteHeadS2, addStem(noteHeadS2.boundingBox))),
            RenderingElementImpl(listOf(noteHeadS1, addStem(noteHeadS1.boundingBox))),
            RenderingElementImpl(listOf(noteHeadS0, addStem(noteHeadS0.boundingBox)))),
            listOf(point1, point2, point3, point4))


    val chord = createChord(listOf(Note(60, NoteType.QUARTER_NOTE), Note(62, NoteType.HALF_NOTE)))
    val chord2 = createChord(listOf(Note(62, NoteType.QUARTER_NOTE), Note(64, NoteType.QUARTER_NOTE)))
    val temporalElementSequence2 = RenderingSequence(listOf(chord, chord2,
            RenderingElementImpl(RenderingElementImpl(listOf(noteHeadS2, addStem(noteHeadS2.boundingBox))),
                    addAdditionalBarLines(Note(56, NoteType.HALF_NOTE)))),
            listOf(point1, point2, point3))

    val path = Paths.get("/home/student/test_output.xml")

//    writeToFile(temporalElementSequence, path)

    writeToFile(temporalElementSequence2, path)

}