package com.kjipo.svg


import com.kjipo.score.RenderingSequence
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.IOException
import java.io.StringWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

val HTML_NAMESPACE = "http://www.w3.org/1999/xhtml"
val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"

fun writeToHtmlFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val document = createHtmlDocument(renderingSequence)
    writeDocumentToFile(document, outputFilePath)
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

    val svgElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")
    svgElement.setAttribute("width", "100%")
    svgElement.setAttribute("height", "100%")
    svgElement.setAttribute("viewBox", renderingSequence.viewBox.let { "${it.xMin} ${it.yMin} ${it.xMax} ${it.yMax}" })
    svgElement.setAttribute("id", "score")
    bodyElement.appendChild(svgElement)

    svgElement.setAttribute("xmlns:xlink", "http://www.w3.org/1999/xlink")

    generateSvgData(renderingSequence, svgElement)

    return document
}


fun writeToFile(renderingSequence: RenderingSequence, outputFilePath: Path) {
    val documentFactory = DocumentBuilderFactory.newInstance()
    val document = documentFactory.newDocumentBuilder().domImplementation.createDocument(SVG_NAMESPACE_URI, "svg", null)
    val rootElement = document.documentElement
    generateSvgData(renderingSequence, rootElement)
    writeDocumentToFile(document, outputFilePath)
}


fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
    val xStart = 100
    val yStart = 400

//    val usedGlyphs = mutableSetOf<String>()
//
//
//    renderingSequence.renderingElements.forEach {
//        it.glyphData?.let {
//            if (!usedGlyphs.contains(it.name)) {
//                val defsElement = svgElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "defs")
//                val pathElement = defsElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "path")
//                pathElement.setAttribute("id", it.name)
//                pathElement.setAttribute("d", transformToPathString(it.pathElements))
//
//                usedGlyphs.add(it.name)
//                defsElement.appendChild(pathElement)
//                svgElement.appendChild(defsElement)
//            }
//        }
//    }


    // TODO Fix method so that it adds elements

//    renderingSequence.renderingElements.forEach {
//        for (pathInterface in it.renderingPath) {
//            addPath(svgElement,
//                    transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
//                    pathInterface.strokeWidth,
//                    it.id)
//        }
//    }

}


@Throws(IOException::class, TransformerException::class)
fun writeDocumentToFile(svgDocument: Document, outputPath: Path) {
    val source = DOMSource(svgDocument)

    Files.newBufferedWriter(outputPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING).use { bufferedWriter ->
        val result = StreamResult(bufferedWriter)
        val transformerFactory = TransformerFactory.newInstance()
        val transformer = transformerFactory.newTransformer()
        // Set output to be HTML. If this is not set it causes
        // empty tags to appear, which causes problems when used
        // with the webview-component
        transformer.setOutputProperty(OutputKeys.METHOD, "html")
        transformer.transform(source, result)
    }
}

@JvmOverloads
fun addPath(node: Node, path: String, strokeWidth: Int, id: String? = null) {
    val path1 = node.ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
    path1.setAttribute("d", path)
    path1.setAttribute("stroke", "blue")
    path1.setAttribute("fill", "yellow")
    if (id != null) {
        path1.setAttribute("id", id)
    }

    path1.setAttribute("stroke-width", strokeWidth.toString())

    node.appendChild(path1)
}

fun addLine(xStart: Int, yStart: Int, xEnd: Int, yEnd: Int, node: Node, strokeWidth: Int) {
    val path1 = node.ownerDocument.createElementNS(SVG_NAMESPACE_URI, "line")
    path1.setAttribute("x1", xStart.toString())
    path1.setAttribute("y1", yStart.toString())
    path1.setAttribute("x2", xEnd.toString())
    path1.setAttribute("y2", yEnd.toString())


    path1.setAttribute("stroke-width", strokeWidth.toString())
    path1.setAttribute("stroke", "black")


    node.appendChild(path1)
}

fun addPathUsingReference(node: Node, reference: String, x: Int, y: Int, id: String?) {
    val useTag = node.ownerDocument.createElementNS(SVG_NAMESPACE_URI, "use")
    useTag.setAttribute("xlink:href", "#$reference")
    useTag.setAttribute("x", x.toString())
    useTag.setAttribute("y", y.toString())
    node.appendChild(useTag)
    if (id != null) {
        useTag.setAttribute("id", id)
    }
}

