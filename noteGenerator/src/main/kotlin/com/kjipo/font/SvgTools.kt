package com.kjipo.font

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.io.BufferedWriter
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption

object SvgTools {
    val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"


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
    fun addPath(node: Node, path: String, strokeWidth: Int, id: Int? = null) {
        val path1 = node.ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
        path1.setAttribute("d", path)
        path1.setAttribute("stroke", "blue")
        path1.setAttribute("fill", "yellow")
        if (id != null) {
            path1.setAttribute("id", "note" + id.toString())
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

    fun addPathUsingReference(node: Node, reference: String, x: Int, y: Int, id: Int?) {
        val useTag = node.ownerDocument.createElementNS(SVG_NAMESPACE_URI, "use")
        useTag.setAttribute("xlink:href", "#$reference")
        useTag.setAttribute("x", x.toString())
        useTag.setAttribute("y", y.toString())
        node.appendChild(useTag)
        if (id != null) {
            useTag.setAttribute("id", "note" + id.toString())
        }
    }

}



