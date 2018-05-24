import kotlin.browser.document


fun loadScore(renderingSequence: RenderingSequence) {
    val xStart = 100
    val yStart = 400
    val usedGlyphs = mutableMapOf<String, GlyphData>()

    val svgElement = document.createElementNS(SVG_NAMESPACE_URI, "svg")

    for (i in 0..renderingSequence.renderingElements.size - 1) {
        val renderingElement = renderingSequence.renderingElements.get(i)

        if(renderingElement.glyphData != null) {
            renderingElement.glyphData?.let {
                if(!usedGlyphs.containsKey(it.name)) {
                    usedGlyphs.put(it.name, it)
                }
                addPathUsingReference(svgElement, it.name, xStart + renderingElement.xPosition, yStart + renderingElement.yPosition, renderingElement.id)
            }
        }
        else {
            for (pathInterface in renderingElement.renderingPath) {
                addPath(svgElement,
                        transformToPathString(translateGlyph(pathInterface, xStart + renderingElement.xPosition, yStart + renderingElement.yPosition)),
                        pathInterface.strokeWidth,
                        // TODO Now multiple paths will have the same ID
                        renderingElement.id)
            }
        }


    }

    if(!usedGlyphs.isEmpty()) {
        val defsElement = svgElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "defs")
        svgElement.appendChild(defsElement)

        usedGlyphs.entries.forEach {
            val pathElement = defsElement.ownerDocument!!.createElementNS(SVG_NAMESPACE_URI, "path")
            pathElement.setAttribute("id", it.key)
            pathElement.setAttribute("d", transformToPathString(it.value.pathElements))

            defsElement.appendChild(pathElement)
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

    loadScore(testScore)

}


