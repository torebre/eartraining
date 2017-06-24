package com.kjipo.svg

import com.kjipo.font.*
import org.apache.batik.dom.svg.SVGDOMImplementation
import org.w3c.dom.Node
import org.w3c.dom.svg.SVGDocument
import org.w3c.dom.svg.SVGSVGElement
import java.nio.file.Path
import java.nio.file.Paths


fun writeToFile(noteSequence: NoteSequence, outputFilePath: Path) {
    val impl = SVGDOMImplementation.getDOMImplementation()
    val svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI
    val doc = impl.createDocument(svgNS, "svg", null)

    val svgDocument = doc as SVGDocument
    val rootElement = svgDocument.rootElement

//    val gElement = rootElement

    // TODO Draw sequence
    drawBarLines(rootElement)

    drawGlyph(10, 10, GlyphFactory.nameGlyphMap.getOrDefault("clefs.G", GlyphData("blank", emptyList())), rootElement)

    SvgTools.writeDocumentToFile(svgDocument, outputFilePath)
}


fun drawBarLines(element: SVGSVGElement) {
    var x = 0
    var y = 0

    val width = 500
    val spaceBetweenLines = 10

    drawLine(x, y, x, y + 4 * spaceBetweenLines, element)
    drawLine(x + width, y, x + width, y + 4 * spaceBetweenLines, element)
    for (i in 0..4) {
        drawLine(x, y, x + width, y, element)
        y += spaceBetweenLines
    }


}



fun drawGlyph(x:Int, y:Int, glyphData: GlyphData, node:Node) {
    SvgTools.addPath(node, transformToPathString(translateGlyph(glyphData, x, y)))
}

fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node)
}


fun main(args: Array<String>) {
    val noteSequence = NoteSequence()

    val path = Paths.get("/home/student/test_output.xml")

    writeToFile(noteSequence, path)

}