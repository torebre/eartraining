package com.kjipo.svg

import com.kjipo.font.SvgTools
import com.kjipo.font.transformToPathString
import com.kjipo.font.translateGlyph
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory

val HTML_NAMESPACE = "http://www.w3.org/1999/xhtml"

fun writeToHtmlFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val documentFactory = DocumentBuilderFactory.newInstance()
    val document = documentFactory.newDocumentBuilder().domImplementation.createDocument(HTML_NAMESPACE, "html", null)
    val rootElement = document.documentElement

    val headElement = document.createElementNS(HTML_NAMESPACE, "head")
    rootElement.appendChild(headElement)

//    val scriptElement = document.createElementNS(HTML_NAMESPACE, "script")
//    scriptElement.setAttribute("src", "jquery.js")
//    scriptElement.setAttribute("type", "text/javascript")
//    headElement.appendChild(scriptElement)

    val bodyElement = document.createElementNS(HTML_NAMESPACE, "body")
    rootElement.appendChild(bodyElement)

    val svgElement = document.createElementNS(SvgTools.SVG_NAMESPACE_URI, "svg")
    svgElement.setAttribute("width", "30cm")
    svgElement.setAttribute("height", "30cm")
    bodyElement.appendChild(svgElement)

    generateSvgData(renderingSequence, svgElement)
    SvgTools.writeDocumentToFile(document, outputFilePath)
}

fun writeToFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val documentFactory = DocumentBuilderFactory.newInstance()
    val document = documentFactory.newDocumentBuilder().domImplementation.createDocument(SvgTools.SVG_NAMESPACE_URI, "svg", null)
    val rootElement = document.documentElement
    generateSvgData(renderingSequence, rootElement)
    SvgTools.writeDocumentToFile(document, outputFilePath)
}

fun generateSvgData(renderingSequence: RenderingSequence, rootElement: Element) {
    var xStart = 100
    val yStart = 400

    // TODO Draw sequence
    drawBarLines(rootElement, xStart, yStart)

    for (i in 0..renderingSequence.renderingElements.size - 1) {
        val renderingElement = renderingSequence.renderingElements.get(i)

        for (pathInterface in renderingElement.renderingPath) {
            SvgTools.addPath(rootElement,
                    transformToPathString(translateGlyph(pathInterface, xStart + renderingElement.xPosition, yStart + renderingElement.yPosition)),
                    pathInterface.strokeWidth,
                    // TODO Now multiple paths will have the same ID
                    renderingElement.id)
        }
    }
}


fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node, strokeWidth: Int) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node, strokeWidth)
}