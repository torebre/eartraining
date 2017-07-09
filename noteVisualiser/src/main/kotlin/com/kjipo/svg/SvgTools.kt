package com.kjipo.svg

import com.kjipo.font.*
import org.apache.batik.dom.svg.SVGDOMImplementation
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGSVGElement
import java.nio.file.Path
import java.nio.file.Paths


fun writeToFile(temporalElementSequence: TemporalElementSequence, outputFilePath: Path) {
    val impl = SVGDOMImplementation.getDOMImplementation()
    val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    val doc = impl.createDocument(svgNS, "svg", null)

    val svgDocument = doc as SVGDocument
    val rootElement = svgDocument.rootElement

    var xStart = 100
    val yStart = 400

    // TODO Draw sequence
    drawBarLines(rootElement, xStart, yStart)

    for (renderingElement in temporalElementSequence.renderingElements) {
        for (pathInterface in renderingElement.renderingPath) {
            drawGlyph(xStart + renderingElement.x, yStart + renderingElement.y, pathInterface, rootElement)
        }

    }


    SvgTools.writeDocumentToFile(svgDocument, outputFilePath)
}


fun drawBarLines(element: SVGSVGElement, xStart: Int, gLine: Int) {
    val width = 500
    val spaceBetweenLines = 20

    var x = xStart
    var y = gLine - spaceBetweenLines * 3

    drawLine(x, y, x, y + 4 * spaceBetweenLines, element)
    drawLine(x + width, y, x + width, y + 4 * spaceBetweenLines, element)
    for (i in 0..4) {
        drawLine(x, y, x + width, y, element)
        y += spaceBetweenLines
    }


}


fun drawGlyph(x: Int, y: Int, pathInterface: PathInterface, node: Node) {
    SvgTools.addPath(node, transformToPathString(translateGlyph(pathInterface, x, y)))
}


fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node)
}


fun main(args: Array<String>) {
    val noteSequence = NoteSequence(listOf(
            GlyphFactory.getGlyph("clefs.G"),
            GlyphFactory.getGlyph("noteheads.s2"),
            GlyphFactory.getGlyph("noteheads.s1"),
            GlyphFactory.getGlyph("noteheads.s0")
//            GlyphFactory.getGlyph("noteheads.s2slash"),
//            GlyphFactory.getGlyph("noteheads.s1slash"),
//            GlyphFactory.getGlyph("noteheads.s0slash"))
    ))

    val x = 0
    val y = 0

    val temporalElementSequence = TemporalElementSequence(listOf(
            RenderingElement(x, y, listOf(GlyphFactory.getGlyph("clefs.G"))),
            RenderingElement(x, y + 50, listOf(GlyphFactory.getGlyph("noteheads.s2"))),
            RenderingElement(x, y + 100, listOf(GlyphFactory.getGlyph("noteheads.s1"))),
            RenderingElement(x, y + 150, listOf(GlyphFactory.getGlyph("noteheads.s0")))))


    val path = Paths.get("/home/student/test_output.xml")

    writeToFile(temporalElementSequence, path)

}