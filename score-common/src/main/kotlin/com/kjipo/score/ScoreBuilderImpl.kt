package com.kjipo.score


class ScoreBuilderImpl(override val debug: Boolean = false) : ScoreBuilderInterface<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    val scoreData = ScoreSetup()
    private var restCounter = 0
    private var noteCounter = 0


    override fun onBarAdded(inputBar: BAR) {
        // TODO Figure out best way to set the value

        inputBar.let {
            val bar = it.barData
            bar.scoreRenderingElements.addAll(currentElements)
            currentElements.clear()
            if (scoreData.bars.isNotEmpty()) {
                // TODO Is this the correct direction? In which direction are the bars added through the method calls?
                bar.previousBar = scoreData.bars.last()
            }
            scoreData.bars.add(bar)
        }
    }

    override fun onNoteAdded(note: NOTE): String {
        val id = "note-${noteCounter++}"
        val noteElement = NoteElement(note.note, note.octave, note.duration, 0, 0, note.beamGroup, id)

        currentElements.add(noteElement)
        scoreData.noteElements.add(noteElement)

        return id
    }

    override fun onRestAdded(rest: REST) {
        val restElement = RestElement(rest.duration, 0, 0, "rest-${restCounter++}")

        currentElements.add(restElement)
        scoreData.noteElements.add(restElement)
    }

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

//    override fun build(): RenderingSequence {
//        // TODO Possible to use immutable lists here?
//        // TODO The position will be wrong when there are multiple bars
//        val renderingElements = mutableListOf<PositionedRenderingElement>()
//
//        scoreData.noteElements.filter { it is NoteElement }
//                .map { it as NoteElement }
//                .forEach {
//                    it.yPosition = calculateVerticalOffset(it.note, it.octave)
//                }
//
//        var barXoffset = 0
//        var barYoffset = 0
//        val barXspace = 0
//        val barYspace = 0
//
//        scoreData.bars.forEach {
//            renderingElements.addAll(it.build(barXoffset, barYoffset))
//            barXoffset += barXspace
//            barYoffset += barYspace
//        }
//
//        val beamGroups = mutableMapOf<Int, MutableCollection<PositionedRenderingElement>>()
//
//        scoreData.noteElements.filter { it is NoteElement }
//                .map { it as NoteElement }
//                .forEach {
//                    if (it.requiresStem()) {
//                        val stem = addStem(it.toRenderingElement().boundingBox)
//                        val stemElement = PositionedRenderingElement(listOf(stem),
//                                findBoundingBox(stem.pathElements),
//                                "stem-${stemCounter++}",
//                                it.xPosition,
//                                it.yPosition)
//
//                        if (beamGroups.containsKey(it.beamGroup)) {
//                            beamGroups.get(it.beamGroup)?.add(stemElement)
//                        } else {
//                            beamGroups.put(it.beamGroup, mutableListOf(stemElement))
//                        }
//                    }
//                }
//
//        beamGroups.forEach {
//            if (it.key == 0) {
//                renderingElements.addAll(it.value)
//            } else {
//                renderingElements.addAll(it.value)
//                renderingElements.add(handleBeams(it.value))
//            }
//        }
//
//        return RenderingSequence(renderingElements, determineViewBox(renderingElements))
//    }
//
//    private fun determineViewBox(renderingElements: Collection<PositionedRenderingElement>): ViewBox {
//        var xMin = 0.0
//        var yMin = 0.0
//        var xMax = 0.0
//        var yMax = 0.0
//        renderingElements.forEach {
//            if (xMin > it.boundingBox.xMin) {
//                xMin = it.boundingBox.xMin
//            }
//            if (xMax < it.boundingBox.xMax) {
//                xMax = it.boundingBox.xMax
//            }
//            if (yMin > it.boundingBox.yMin) {
//                yMin = it.boundingBox.yMin
//            }
//            if (yMax < it.boundingBox.yMax) {
//                yMax = it.boundingBox.yMax
//            }
//        }
//
//        xMin -= LEFT_MARGIN
//        yMin -= TOP_MARGIN
//        xMax += RIGHT_MARGIN
//        yMax += BOTTOM_MARGIN
//
//        // This is to try to get to cropping when xMin and/or yMin are less than 0
//        if (xMin < 0) {
//            xMax += abs(xMin)
//        }
//        if (yMin < 0) {
//            yMax += abs(yMin)
//        }
//
//        return ViewBox(xMin.roundToInt(),
//                yMin.roundToInt(),
//                xMax.roundToInt(),
//                yMax.roundToInt())
//    }


//    private fun handleBeams(beamGroup: Collection<PositionedRenderingElement>): PositionedRenderingElement {
//        val stemMinimum = beamGroup.reduce({ s, t ->
//            s.let {
//                if (it.xPosition < t.xPosition) {
//                    it
//                } else {
//                    t
//                }
//            }
//        })
//
//        val stemMaximum = beamGroup.reduce({ s, t ->
//            s.let {
//                if (it.xPosition > t.xPosition) {
//                    it
//                } else {
//                    t
//                }
//            }
//        })
//
//        val height = stemMinimum.boundingBox.height()
//        val yStart = minOf(stemMinimum.yPosition.toDouble() + height, stemMaximum.yPosition.toDouble() + height)
//        val yEnd = maxOf(stemMinimum.yPosition.toDouble() + height, stemMaximum.yPosition.toDouble() + height)
//        val boundingBoxHeight = yEnd - yStart
//
//        val beamElement = addBeam(0, 0,
//                stemMaximum.xPosition.minus(stemMinimum.xPosition),
//                -boundingBoxHeight.toInt(),
//                // TODO This probably does not work now
//                stemMinimum.boundingBox.width().toInt(),
//                -height.toInt()
//        )
//
//        return PositionedRenderingElement(listOf(beamElement),
//                findBoundingBox(beamElement.pathElements),
//                "beam-${beamCounter++}",
//                stemMinimum.xPosition,
//                stemMinimum.yPosition)
//    }

//    fun findNote(elementId: String): NoteElement? {
//        return scoreData.noteElements.filter { it is NoteElement }
//                .map { it as NoteElement }
//                .find { it.id == elementId }
//    }

    override fun build() = scoreData.build()

}