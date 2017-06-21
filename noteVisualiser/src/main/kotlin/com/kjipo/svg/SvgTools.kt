package com.kjipo.svg

import com.kjipo.font.SvgTools
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

    SvgTools.writeDocumentToFile(svgDocument, outputFilePath)
}


fun drawBarLines(element: SVGSVGElement) {
    var x = 0
    var y = 0

    val width = 1000

    drawLine(x, y, x + width, y + 1000, element)


}

fun drawLine(xStart:Int, yStart:Int, xEnd:Int, yEnd:Int, node: Node) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node, node.ownerDocument);
}


fun main(args:Array<String>) {
    val noteSequence = NoteSequence()

    val path = Paths.get("/home/student/test_output.xml")

    writeToFile(noteSequence, path)

}