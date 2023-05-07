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
    private val elementIdGroupMap = mutableMapOf<String, MutableSet<Element>>()

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
            renderElement(positionedRenderingElement, svgElement)
        }
    }

    private fun renderElement(
        positionedRenderingElement: PositionedRenderingElementParent,
        svgElement: Element
    ) {
        val elementToAddRenderingElementsTo = setupSvgGroupingElement(positionedRenderingElement, svgElement)

//        logger.debug { "Rendering element with ID: ${positionedRenderingElement.id}. Element ID: ${positionedRenderingElement.properties[ELEMENT_ID]}" }

        positionedRenderingElement.properties[ELEMENT_ID]?.let { elementId ->
            if (elementIdGroupMap.contains(elementId)) {
                elementIdGroupMap[elementId]?.add(elementToAddRenderingElementsTo)
            } else {
                elementIdGroupMap[elementId] = mutableSetOf(elementToAddRenderingElementsTo)
            }
        }
        addPositionRenderingElements(listOf(positionedRenderingElement), elementToAddRenderingElementsTo)
    }

    /**
     * This will return an element where the render data should be added to.
     */
    private fun setupSvgGroupingElement(
        positionedRenderingElement: PositionedRenderingElementParent,
        svgElement: Element
    ) = when (positionedRenderingElement) {
        is TranslatedRenderingElement -> {
            setupTranslatedElement(svgElement, positionedRenderingElement.translation)
        }

        is TranslatedRenderingElementUsingReference -> {
            setupTranslatedElement(svgElement, positionedRenderingElement.translation)
        }

        is AbsolutelyPositionedRenderingElement -> {
            setupGroupElement(svgElement)
        }
    }

    /**
     * Applies an update to the SVG. This method is used when there is a change set
     * to be applied, instead of having to rerender the entire SVG.
     */
    fun updateSvg(renderingSequenceUpdate: RenderingSequenceUpdate, svgElement: Element) {
        // TODO Need to look closer at the update logic. Right now it takes the ELEMENT_ID property,
        //  which links graphical elements to score elements, and removes the elements associated
        //  with it from the score, before adding the updated versions. It is not certain that this
        //  will work in all settings
        val scoreRenderingElementsAffected =
            renderingSequenceUpdate.renderGroupUpdates.map { it.value.properties[ELEMENT_ID] }.filterNotNull().toSet()

        scoreRenderingElementsAffected.forEach { scoreRenderingElementId ->
            elementIdGroupMap.remove(scoreRenderingElementId)?.let { elementsBelongingToScoreElement ->
                logger.debug { "Removing element with key: $scoreRenderingElementId" }
                elementsBelongingToScoreElement.forEach {
                    it.remove()
                }
            }
        }

        createElementsBasedOnRenderingSequenceUpdate(renderingSequenceUpdate, svgElement)
    }

    private fun createElementsBasedOnRenderingSequenceUpdate(
        renderingSequenceUpdate: RenderingSequenceUpdate,
        svgElement: Element
    ) {
        renderingSequenceUpdate.renderGroupUpdates.forEach { renderGroupUpdate ->
            val svgGroupingElement = setupSvgGroupingElement(renderGroupUpdate.value, svgElement)

            renderGroupUpdate.value.properties[ELEMENT_ID]?.let { elementId ->
                if (elementIdGroupMap.contains(elementId)) {
                    elementIdGroupMap[elementId]?.add(svgGroupingElement)
                } else {
                    elementIdGroupMap[elementId] = mutableSetOf(svgGroupingElement)
                }
            }
            addPositionRenderingElements(listOf(renderGroupUpdate.value), svgGroupingElement)
        }
    }

    private fun setupGroupElement(svgElement: Element): Element {
        return svgElement.ownerDocument!!.let {
            val groupingElement = it.createElementNS(SVG_NAMESPACE_URI, "g")

            svgElement.appendChild(groupingElement)
            groupingElement
        }
    }

    private fun setupTranslatedElement(svgElement: Element, translation: Translation): Element {
        return svgElement.ownerDocument!!.let {
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
                    isClickable = false,
                )
            }
            svgElement.appendChild(defsTag)
        }
    }

    private fun addPositionRenderingElements(
        renderingElements: Collection<PositionedRenderingElementParent>,
        elementToAddTo: Element
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
                            elementToAddTo,
                            transformToPathString(translateGlyph(pathInterface, 0.0, 0.0)),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill,
                            extraAttributes,
                            renderingElement.isClickable,
                            pathInterface.stroke
                        )
                    }
                }

                is AbsolutelyPositionedRenderingElement -> {
                    for (pathInterface in renderingElement.renderingPath) {
                        addPath(
                            elementToAddTo,
                            transformToPathString(pathInterface),
                            pathInterface.strokeWidth,
                            renderingElement.id,
                            pathInterface.fill,
                            extraAttributes,
                            renderingElement.isClickable,
                            pathInterface.stroke
                        )?.let { pathElement ->
                            idSvgElementMap.put(renderingElement.id, pathElement)
                        }
                    }
                }

                is TranslatedRenderingElementUsingReference -> {
                    addPathUsingReference(
                        elementToAddTo,
                        renderingElement.typeId,
                        renderingElement.id,
                        extraAttributes,
                        renderingElement.isClickable
                    )
                    idSvgElementMap[renderingElement.id] = elementToAddTo
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
        isClickable: Boolean,
        stroke: String? = null
    ): Element? {
        return node.ownerDocument?.let { ownerDocument ->
            val path1 = ownerDocument.createElementNS(SVG_NAMESPACE_URI, "path")
            path1.setAttribute("d", path)
            fill?.let { path1.setAttribute("fill", it) }
            stroke?.let { path1.setAttribute("stroke", it) }
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