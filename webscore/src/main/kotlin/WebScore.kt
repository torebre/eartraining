import com.kjipo.score.RenderingSequence
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.get
import kotlin.browser.document


class WebScore(renderingSequence: RenderingSequence) {
    val SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg"

    val idSvgElementMap = mutableMapOf<String, Element>()

    init {
        loadScore(renderingSequence)
    }


    fun loadScore(renderingSequence: RenderingSequence) {
        val xStart = 100
        val yStart = 400
//        val usedGlyphs = mutableMapOf<String, GlyphData>()

        val divScoreElement = document.getElementById("score")
        val svgElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")

        // TODO Set proper view box
        svgElement.setAttribute("viewBox", "0 0 2000 2000")
        generateSvgData(renderingSequence, svgElement)

        divScoreElement?.appendChild(svgElement)
    }


    fun highlight(id: String) {
        idSvgElementMap[id]?.setAttribute("fill", "red")

    }


    fun move(id: String, to: Int) {

        // TODO Just her for testing
        val animateElement = document.createElementNS(SVG_NAMESPACE_URI, "animate")
        animateElement.setAttribute("attributeName", "opacity")
        animateElement.setAttribute("from", "1")
        animateElement.setAttribute("to", "0")
        animateElement.setAttribute("repeatCount", "indefinite")
        animateElement.setAttribute("dur", "5s")
        animateElement.setAttribute("attributeType", "CSS")

        idSvgElementMap[id]?.appendChild(animateElement)

    }


    private fun generateSvgData(renderingSequence: RenderingSequence, svgElement: Element) {
        val xStart = 100
        val yStart = 400
//        val usedGlyphs = mutableSetOf<String>()


//        renderingSequence.renderingElements.forEach {
//            it.glyphData?.let {
//                if (!usedGlyphs.contains(it.name)) {
//                    val defsElement = svgElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "defs")
//                    val pathElement = defsElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "path")
//                    pathElement.setAttribute("id", it.name)
//                    pathElement.setAttribute("d", transformToPathString(it.pathElements))
//
//                    usedGlyphs.add(it.name)
//                    defsElement.appendChild(pathElement)
//                    svgElement.appendChild(defsElement)
//                }
//            }
//        }

        renderingSequence.renderingElements.forEach {
            if (it.glyphData != null) {

                // TODO Why does using references not work here?

                // TODO The ID setup will only work if there is one path

                it.glyphData?.let { glyphData ->
                    for (pathInterface in it.renderingPath) {
                        val path = addPath(svgElement,
                                transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
                                pathInterface.strokeWidth,
                                it.id)



                        println("Path: ${path?.id ?: "none"}")

                        path?.let { element ->
                            idSvgElementMap.put(it.id, element)
                        }
                    }


//                addPathUsingReference(svgElement, glyphData.name, xStart + it.xPosition, yStart + it.yPosition, it.id)
                }
            } else {
                for (pathInterface in it.renderingPath) {
                    addPath(svgElement,
                            transformToPathString(translateGlyph(pathInterface, xStart + it.xPosition, yStart + it.yPosition)),
                            pathInterface.strokeWidth,
                            it.id)?.let { element ->
                        idSvgElementMap.put(it.id, element)
                    }


                }
            }
        }

    }


    private fun addPath(node: Node, path: String, strokeWidth: Int, id: String?): Element? {
        return node.ownerDocument?.let {
            val path1 = it.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            path1.setAttribute("stroke", "blue")
            path1.setAttribute("fill", "yellow")
            id?.let { path1.setAttribute("id", it) }
            path1.setAttribute("stroke-width", strokeWidth.toString())

            node.appendChild(path1)
            path1
        }
    }


    private fun addPathUsingReference(node: Node, reference: String, x: Int, y: Int, id: String?) {
        node.ownerDocument?.let {
            val useTag = it.createElementNS(SVG_NAMESPACE_URI, "use")
            useTag.setAttribute("xlink:href", "#$reference")
            useTag.setAttribute("x", x.toString())
            useTag.setAttribute("y", y.toString())
            node.appendChild(useTag)
            if (id != null) {
                useTag.setAttribute("id", id)
            }
        }
    }


}