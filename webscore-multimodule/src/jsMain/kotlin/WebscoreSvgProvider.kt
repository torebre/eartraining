import com.kjipo.handler.ScoreProviderInterface
import com.kjipo.score.*
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

        svgElement.clear()
        renderingSequence.viewBox?.run {
            svgElement.setAttribute(
                "viewBox",
                "$xMin $yMin $xMax $yMax"
            )
        }
        setupDefinitionTag(svgElement, renderingSequence)

        renderingSequence.renderGroups.forEach { positionedRenderingElement ->
            val elementToAddRenderingElementsTo = when (positionedRenderingElement) {
                is TranslatedRenderingElement -> {
                    setupTranslatedElement(svgElement, positionedRenderingElement.translation)
                }
                is TranslatedRenderingElementUsingReference -> {
                    setupTranslatedElement(svgElement, positionedRenderingElement.translation)
                }
                is AbsolutelyPositionedRenderingElement -> {
                    svgElement
                }
            }
            elementToAddRenderingElementsTo?.let {
                addPositionRenderingElements(listOf(positionedRenderingElement), it)
            }
        }
    }

    private fun setupTranslatedElement(svgElement: Element, translation: Translation): Element? {
        return svgElement.ownerDocument?.let {
            val groupingElement = it.createElementNS(SVG_NAMESPACE_URI, "g")
            groupingElement.setAttribute("transform", "translate(${translation.xShift}, ${translation.yShift})")

            svgElement.appendChild(groupingElement)
            groupingElement
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
                    definition.key,
                    isClickable = false
                )
            }
            svgElement.appendChild(defsTag)
        }
    }

    private fun addPositionRenderingElements(
        renderingElements: Collection<PositionedRenderingElementParent>,
        element: Element
    ) {
        for (renderingElement in renderingElements) {
            val groupClass = renderingElement.groupClass
            val extraAttributes = if (groupClass != null) {
                mapOf(Pair("class", groupClass))
            } else {
                emptyMap()
            }

            when (renderingElement) {
                is TranslatedRenderingElement -> {
                    renderingElement.renderingPath.forEach { pathInterface ->
                        addPath(
                            element,
                            transformToPathString(translateGlyph(pathInterface, 0, 0)),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill,
                            extraAttributes,
                            renderingElement.isClickable
                        )
                    }
                }
                is AbsolutelyPositionedRenderingElement -> {
                    for (pathInterface in renderingElement.renderingPath) {
                        addPath(
                            element,
                            transformToPathString(pathInterface),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill,
                            extraAttributes,
                            renderingElement.isClickable
                        )?.let { pathElement ->
                            idSvgElementMap.put(renderingElement.id, pathElement)
                        }
                    }
                }
                is TranslatedRenderingElementUsingReference -> {
                    addPathUsingReference(
                        element,
                        renderingElement.typeId,
                        renderingElement.id,
                        extraAttributes,
                        renderingElement.isClickable
                    )
                    idSvgElementMap[renderingElement.id] = element
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
        extraAttributes: Map<String, String> = emptyMap(),
        isClickable: Boolean
    ): Element? {
        return node.ownerDocument?.let { ownerDocument ->
            val path1 = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            fill?.let { path1.setAttribute("fill", it) }
            path1.setAttribute("id", id)
            path1.setAttribute("stroke-width", strokeWidth.toString())
            if (isClickable) {
                // TODO Give value as input parameter to attribute
                path1.setAttribute("pointer-events", "test")
            }

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
        id: String,
        extraAttributes: Map<String, String> = emptyMap(),
        isClickable: Boolean
    ) {
        node.ownerDocument?.let { ownerDocument ->
            val useTag = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "use")
            useTag.setAttribute("href", "#$reference")
            useTag.setAttribute("id", id)

            if (isClickable) {
                // TODO Give value as input parameter to attribute
                useTag.setAttribute("pointer-events", "test")
            }

            extraAttributes.forEach {
                useTag.setAttribute(it.key, it.value)
            }
            node.appendChild(useTag)
        }
    }

    fun getHighlightForId(id: String): Collection<String> {
        return scoreHandler.getHighlightMap()[id] ?: emptySet()
    }

    fun getElement(id: String): Element? {
        return idSvgElementMap[id]
    }

    private fun transformJsonToRenderingSequence(jsonData: String): RenderingSequence {
        return Json.decodeFromString(RenderingSequence.serializer(), jsonData)
    }

}