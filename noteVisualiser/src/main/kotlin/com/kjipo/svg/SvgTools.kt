package com.kjipo.svg

import com.kjipo.font.GlyphData
import com.kjipo.font.SvgTools
import com.kjipo.font.transformToPathString
import com.kjipo.font.translateGlyph
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.StringWriter
import java.nio.file.Path
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

val HTML_NAMESPACE = "http://www.w3.org/1999/xhtml"

fun writeToHtmlFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val document = createHtmlDocument(renderingSequence)
    SvgTools.writeDocumentToFile(document, outputFilePath)
}

fun createHtmlDocumentString(renderingSequence: RenderingSequence): String {
    val source = DOMSource(createHtmlDocument(renderingSequence))

    val stringWriter = StringWriter()
    val result = StreamResult(stringWriter)
    val transformerFactory = TransformerFactory.newInstance()
    val transformer = transformerFactory.newTransformer()
    // Set output to be HTML. If this is not set it causes
    // empty tags to appear, which causes problems when used
    // with the webview-component
    transformer.setOutputProperty(OutputKeys.METHOD, "html")
    transformer.transform(source, result)

    return stringWriter.toString()
}

fun createHtmlDocument(renderingSequence: RenderingSequence): Document {
    val documentFactory = DocumentBuilderFactory.newInstance()

    val document = documentFactory.newDocumentBuilder().domImplementation.createDocument(HTML_NAMESPACE, "html", null)
    val rootElement = document.documentElement

    val headElement = document.createElementNS(HTML_NAMESPACE, "head")
    rootElement.appendChild(headElement)

    val bodyElement = document.createElementNS(HTML_NAMESPACE, "body")
    rootElement.appendChild(bodyElement)

    val svgElement = document.createElementNS(SvgTools.SVG_NAMESPACE_URI, "svg")
    svgElement.setAttribute("viewBox", renderingSequence.viewBox.let { "${it.xMin} ${it.yMin} ${it.xMax} ${it.yMax}" })
    svgElement.setAttribute("id", "score")
    bodyElement.appendChild(svgElement)

    svgElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink")

    generateSvgData(renderingSequence, svgElement)

    return document
}


fun writeToFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val documentFactory = DocumentBuilderFactory.newInstance()
    val document = documentFactory.newDocumentBuilder().domImplementation.createDocument(SvgTools.SVG_NAMESPACE_URI, "svg", null)
    val rootElement = document.documentElement
    generateSvgData(renderingSequence, rootElement)
    SvgTools.writeDocumentToFile(document, outputFilePath)
}

fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
    val xStart = 100
    val yStart = 400
    val usedGlyphs = mutableMapOf<String, GlyphData>()

    for (i in 0..renderingSequence.renderingElements.size - 1) {
        val renderingElement = renderingSequence.renderingElements.get(i)

        if(renderingElement.glyphData != null) {
            renderingElement.glyphData?.let {
                usedGlyphs.putIfAbsent(it.name, it)
                SvgTools.addPathUsingReference(svgElement, it.name, xStart + renderingElement.xPosition, yStart + renderingElement.yPosition, renderingElement.id)
            }
        }
        else {
            for (pathInterface in renderingElement.renderingPath) {
                SvgTools.addPath(svgElement,
                        transformToPathString(translateGlyph(pathInterface, xStart + renderingElement.xPosition, yStart + renderingElement.yPosition)),
                        pathInterface.strokeWidth,
                        // TODO Now multiple paths will have the same ID
                        renderingElement.id)
            }
        }


    }

    if(!usedGlyphs.isEmpty()) {
        val defsElement = svgElement.ownerDocument.createElementNS(SvgTools.SVG_NAMESPACE_URI, "defs")
        svgElement.appendChild(defsElement)

        usedGlyphs.entries.forEach {
            val pathElement = defsElement.ownerDocument.createElementNS(SvgTools.SVG_NAMESPACE_URI, "path")
            pathElement.setAttribute("id", it.key)
            pathElement.setAttribute("d", transformToPathString(it.value.pathElements))

            defsElement.appendChild(pathElement)
        }
    }

}


fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node, strokeWidth: Int) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node, strokeWidth)
}