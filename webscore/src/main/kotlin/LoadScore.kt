import com.kjipo.score.*
import com.kjipo.svg.GlyphData
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import kotlinx.html.dom.append
import kotlinx.html.dom.create
import kotlinx.html.js.h1
import kotlinx.html.js.h2
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.Node
import kotlin.browser.document
import kotlin.browser.window


val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"


fun loadScore(renderingSequence: RenderingSequence) {
    val xStart = 100
    val yStart = 400
    val usedGlyphs = mutableMapOf<String, GlyphData>()

    val divScoreElement = document.getElementById("score")
    val svgElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")

    // TODO Set proper view box
    svgElement.setAttribute("viewBox", "0 0 2000 2000")
    generateSvgData(renderingSequence, svgElement)

    divScoreElement?.appendChild(svgElement)
}


fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
    val xStart = 100
    val yStart = 400
    val usedGlyphs = mutableSetOf<String>()


    renderingSequence.renderingElements.forEach {
        it.glyphData?.let {
            if (!usedGlyphs.contains(it.name)) {
                val defsElement = svgElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "defs")
                val pathElement = defsElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "path")
                pathElement.setAttribute("id", it.name)
                pathElement.setAttribute("d", transformToPathString(it.pathElements))

                usedGlyphs.add(it.name)
                defsElement.appendChild(pathElement)
                svgElement.appendChild(defsElement)
            }
        }
    }

    renderingSequence.renderingElements.forEach {
        if (it.glyphData != null) {

            // TODO Why does not using references work here?

            it.glyphData?.let { glyphData ->
                for (pathInterface in it.renderingPath) {
                    addPath(svgElement,
                            transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
                            pathInterface.strokeWidth,
                            // TODO Now multiple paths will have the same ID
                            it.id)
                }

//                addPathUsingReference(svgElement, glyphData.name, xStart + it.xPosition, yStart + it.yPosition, it.id)
            }
        } else {
            for (pathInterface in it.renderingPath) {
                addPath(svgElement,
                        transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
                        pathInterface.strokeWidth,
                        // TODO Now multiple paths will have the same ID
                        it.id)
            }
        }
    }

}


fun addPath(node: Node, path: String, strokeWidth: Int, id: Int? = null) {
    node.ownerDocument?.let {
        val path1 = it.createElementNS(SVG_NAMESPACE_URI, "path")
        path1.setAttribute("d", path)
        path1.setAttribute("stroke", "blue")
        path1.setAttribute("fill", "yellow")
        if (id != null) {
            path1.setAttribute("id", "note" + id.toString())
        }

        path1.setAttribute("stroke-width", strokeWidth.toString())

        node.appendChild(path1)
    }
}


fun addPathUsingReference(node: Node, reference: String, x: Int, y: Int, id: Int?) {
    node.ownerDocument?.let {
        val useTag = it.createElementNS(SVG_NAMESPACE_URI, "use")
        useTag.setAttribute("xlink:href", "#$reference")
        useTag.setAttribute("x", x.toString())
        useTag.setAttribute("y", y.toString())
        node.appendChild(useTag)
        if (id != null) {
            useTag.setAttribute("id", "note" + id.toString())
        }
    }
}


fun main(args: Array<String>) {
    val testScore = createScore().score {
        bar {
            clef = Clef.G
            timeSignature = TimeSignature(4, 4)

            note {
                note = NoteType.A
                duration = Duration.QUARTER
                octave = 4
            }

            note {
                note = NoteType.H
                duration = Duration.QUARTER
                octave = 4
            }

            note {
                note = NoteType.C
                duration = Duration.QUARTER
            }

            rest {
                duration = Duration.QUARTER
            }

        }

    }



    window.onload = {
        loadScore(testScore)
    }


}


