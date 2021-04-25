import com.kjipo.handler.ScoreProviderInterface
import com.kjipo.score.PositionedRenderingElement
import com.kjipo.score.RenderingSequence
import com.kjipo.score.Translation
import com.kjipo.svg.transformToPathString
import com.kjipo.svg.translateGlyph
import kotlinx.dom.clear
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.w3c.dom.Element
import org.w3c.dom.Node


class WebscoreSvgProvider(private val scoreHandler: ScoreProviderInterface) {

    private val idSvgElementMap = mutableMapOf<String, Element>()

    private val logger = KotlinLogging.logger {}

    fun generateSvgData(svgElement: Element) {
        val renderingSequence = transformJsonToRenderingSequence(scoreHandler.getScoreAsJson())

        logger.debug { "Generating data" }

        svgElement.clear()
        svgElement.setAttribute(
            "viewBox",
            "${renderingSequence.viewBox.xMin} ${renderingSequence.viewBox.yMin} ${renderingSequence.viewBox.xMax} ${renderingSequence.viewBox.yMax}"
        )
        setupDefinitionTag(svgElement, renderingSequence)

        renderingSequence.renderGroups.forEach { positionedRenderingElement ->
            val elementToAddRenderingElementsTo = if (positionedRenderingElement.translation != null) {
                val translation = positionedRenderingElement.translation ?: Translation(0, 0)
                svgElement.ownerDocument?.let {
                    val groupingElement = it.createElementNS(SVG_NAMESPACE_URI, "g")
                    groupingElement.setAttribute("transform", "translate(${translation.xShift}, ${translation.yShift})")

                    svgElement.appendChild(groupingElement)
                    groupingElement
                }
            } else {
                svgElement
            }

            elementToAddRenderingElementsTo?.let {
                addPositionRenderingElements(listOf(positionedRenderingElement), it)
            }
        }
    }

    private fun setupDefinitionTag(svgElement: Element, renderingSequence: RenderingSequence) {
        svgElement.ownerDocument?.let {
            val defsTag = it.createElementNS(SVG_NAMESPACE_URI, "defs")

            for (definition in renderingSequence.definitions) {
                addPath(
                    defsTag,
                    transformToPathString(definition.value.pathElements),
                    definition.value.strokeWidth,
                    definition.key
                )
            }

            svgElement.appendChild(defsTag)
        }
    }

    private fun addPositionRenderingElements(
        renderingElements: Collection<PositionedRenderingElement>,
        element: Element
    ) {
        for (renderingElement in renderingElements) {
            val groupClass = renderingElement.groupClass
            val extraAttributes = if (groupClass != null) {
                mapOf(Pair("class", groupClass))
            } else {
                emptyMap()
            }

            if (renderingElement.typeId != null) {
                renderingElement.typeId?.let { typeId ->
                    addPathUsingReference(element, typeId, renderingElement, extraAttributes)
                    idSvgElementMap.put(renderingElement.id, element)
                }
            } else {
                for (pathInterface in renderingElement.renderingPath) {
                    addPath(
                        element,
                        transformToPathString(
                            translateGlyph(
                                pathInterface,
                                renderingElement.xPosition,
                                renderingElement.yPosition
                            )
                        ),
                        pathInterface.strokeWidth,
                        renderingElement.id,
                        pathInterface.fill,
                        extraAttributes
                    )?.let { pathElement ->
                        idSvgElementMap.put(renderingElement.id, pathElement)
                    }
                }
            }
        }
    }

    private fun addPath(
        node: Node,
        path: String,
        strokeWidth: Int,
        id: String,
        fill: String? = null,
        extraAttributes: Map<String, String> = emptyMap()
    ): Element? {
        return node.ownerDocument?.let { ownerDocument ->
            val path1 = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            fill?.let { path1.setAttribute("fill", it) }
            path1.setAttribute("id", id)
            path1.setAttribute("stroke-width", strokeWidth.toString())

            extraAttributes.forEach {
                path1.setAttribute(it.key, it.value)
            }

            node.appendChild(path1)
            path1
        }
    }

    private fun addPathUsingReference(
        node: Node,
        reference: String,
        positionedRenderingElement: PositionedRenderingElement,
        extraAttributes: Map<String, String> = emptyMap()
    ) {
        node.ownerDocument?.let { ownerDocument ->
            val useTag = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "use")
            useTag.setAttribute("href", "#$reference")
            useTag.setAttribute("id", positionedRenderingElement.id)

            extraAttributes.forEach {
                useTag.setAttribute(it.key, it.value)
            }

            if (positionedRenderingElement.xTranslate != 0
                || positionedRenderingElement.yTranslate != 0
            ) {
                val groupingElement = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "g")
                groupingElement.setAttribute(
                    "transform",
                    "translate(${positionedRenderingElement.xTranslate}, ${positionedRenderingElement.yTranslate})"
                )
                groupingElement.appendChild(useTag)
                node.appendChild(groupingElement)
            } else {
                node.appendChild(useTag)
            }
        }
    }

    fun getHighlightForId(id: String): Collection<String> {
        logger.debug { "Highlight map: ${scoreHandler.getHighlightMap()}" }

        val elementsToHighlight = scoreHandler.getHighlightMap()[id] ?: emptySet()

        logger.debug { "Elements to highlight: ${elementsToHighlight}" }

        return elementsToHighlight
    }

    fun getElement(id: String): Element? {
        return idSvgElementMap[id]
    }

    private fun transformJsonToRenderingSequence(jsonData: String): RenderingSequence {
        return Json.decodeFromString(RenderingSequence.serializer(), jsonData)
    }

}