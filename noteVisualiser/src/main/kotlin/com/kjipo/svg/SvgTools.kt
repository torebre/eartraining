package com.kjipo.svg

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
import kotlin.streams.toList

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
    svgElement.setAttribute("width", "30cm")
    svgElement.setAttribute("height", "30cm")
    svgElement.setAttribute("id", "score")
    bodyElement.appendChild(svgElement)

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

fun generateSvgData(renderingSequence: RenderingSequence, rootElement: Element) {
    var xStart = 100
    val yStart = 400

    drawBarLines(rootElement, xStart, yStart)

    val tieGroups = mutableMapOf<Int, MutableCollection<RenderingElement>>()

    for (i in 0..renderingSequence.renderingElements.size - 1) {
        val renderingElement = renderingSequence.renderingElements.get(i)

        if (renderingElement.tieGroup != 0) {
            tieGroups.compute(renderingElement.tieGroup, { tieGroup, elements ->
                if (elements == null) {
                    mutableListOf(renderingElement)
                } else {
                    elements.add(renderingElement)
                    elements
                }
            })
        }

        for (pathInterface in renderingElement.renderingPath) {
            SvgTools.addPath(rootElement,
                    transformToPathString(translateGlyph(pathInterface, xStart + renderingElement.xPosition, yStart + renderingElement.yPosition)),
                    pathInterface.strokeWidth,
                    // TODO Now multiple paths will have the same ID
                    renderingElement.id)
        }
    }

    // TODO Remove use of system out
    println("Tie groups: $tieGroups")

    tieGroups.forEach({ tieGroup, renderingElements ->
        val stemElements = renderingElements.stream()
                .map { it -> it.stem }
                .toList()

        val stemMinimum = stemElements.reduceRight({ s, t ->
            if (t == null) {
                s
            } else {
                s?.let {
                    if (it.xPosition < t.xPosition) {
                        it
                    } else {
                        t
                    }
                }
            }
        })

        val stemMaximum = stemElements.reduceRight({ s, t ->
            if (t == null) {
                s
            } else {
                s?.let {
                    if (it.xPosition > t.xPosition) {
                        it
                    } else {
                        t
                    }
                }
            }
        })

        println("Group: $tieGroup. Minimum: $stemMinimum. Maximum: $stemMaximum")

    })


}


fun drawLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node, strokeWidth: Int) {
    SvgTools.addLine(xStart, yStart, xEnd, yEnd, node, strokeWidth)
}