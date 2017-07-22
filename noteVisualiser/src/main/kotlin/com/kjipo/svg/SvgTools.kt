package com.kjipo.svg

import com.kjipo.font.PathInterface
import com.kjipo.font.SvgTools
import com.kjipo.font.transformToPathString
import com.kjipo.font.translateGlyph
import org.w3c.dom.Node
import java.nio.file.Path
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

        for (pathInterface in renderingElement.renderingPath) {
            drawGlyph(xStart + renderingElement.xPosition, yStart + renderingElement.yPosition, pathInterface, rootElement)
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