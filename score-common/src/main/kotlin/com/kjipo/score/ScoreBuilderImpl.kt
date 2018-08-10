package com.kjipo.score

import com.kjipo.svg.findBoundingBox


class ScoreBuilderImpl(override val debug: Boolean = false) : ScoreBuilderInterface<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    val noteElements = mutableListOf<TemporalElement>()
    private val bars = mutableListOf<BAR>()

    private var restCounter = 0
    private var noteCounter = 0
    private var stemCounter = 0
    private var beamCounter = 0


    override fun onBarAdded(bar: BAR) {
        // TODO Figure out best way to set the value
        bar.scoreRenderingElements.addAll(currentElements)
        currentElements.clear()
        if (bars.isNotEmpty()) {
            // TODO Is this the correct direction? In which direction are the bars added through the method calls?
            bar.previousBar = bars.last()


//            bar.scoreRenderingElements.filter { it is NoteElement }.map { it as NoteElement }.forEach { it.bar = bar }
        }
        bars.add(bar)
    }

    override fun onNoteAdded(note: NOTE) {
        val noteElement = NoteElement(note.note, note.octave, note.duration, 0, 0, note.beamGroup, note.id
                ?: "note-${noteCounter++}")

        currentElements.add(noteElement)
        noteElements.add(noteElement)
    }

    override fun onRestAdded(rest: REST) {
        val restElement = RestElement(rest.duration, 0, 0, "rest-${restCounter++}")

        currentElements.add(restElement)
        noteElements.add(restElement)
    }

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingElements = mutableListOf<PositionedRenderingElement>()

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach {
                    it.yPosition = calculateVerticalOffset(it.note, it.octave)
                }

        var barXoffset = 0
        var barYoffset = 0
        val barXspace = 0
        val barYspace = 200

        bars.forEach {
            renderingElements.addAll(it.build(barXoffset, barYoffset))
            barXoffset += barXspace
            barYoffset += barYspace
        }

        val beamGroups = mutableMapOf<Int, MutableCollection<PositionedRenderingElement>>()

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach {
                    if (it.requiresStem()) {
                        val stem = addStem(it.toRenderingElement().boundingBox)
                        val stemElement = PositionedRenderingElement(listOf(stem),
                                findBoundingBox(stem.pathElements),
                                "stem-${stemCounter++}",
                                it.xPosition,
                                it.yPosition)

                        if (beamGroups.containsKey(it.beamGroup)) {
                            beamGroups.get(it.beamGroup)?.add(stemElement)
                        } else {
                            beamGroups.put(it.beamGroup, mutableListOf(stemElement))
                        }
                    }
                }

        beamGroups.forEach {
            if (it.key == 0) {
                renderingElements.addAll(it.value)
            } else {
                renderingElements.addAll(it.value)
                renderingElements.add(handleBeams(it.value))
            }
        }

        // TODO Set proper view box
        return RenderingSequence(renderingElements, ViewBox(0, 0, 2000, 2000))
    }


    private fun handleBeams(beamGroup: Collection<PositionedRenderingElement>): PositionedRenderingElement {
        val stemMinimum = beamGroup.reduce({ s, t ->
            s.let {
                if (it.xPosition < t.xPosition) {
                    it
                } else {
                    t
                }
            }
        })

        val stemMaximum = beamGroup.reduce({ s, t ->
            s.let {
                if (it.xPosition > t.xPosition) {
                    it
                } else {
                    t
                }
            }
        })

        val height = stemMinimum.boundingBox.height()
        val yStart = minOf(stemMinimum.yPosition.toDouble() + height, stemMaximum.yPosition.toDouble() + height)
        val yEnd = maxOf(stemMinimum.yPosition.toDouble() + height, stemMaximum.yPosition.toDouble() + height)
        val boundingBoxHeight = yEnd - yStart

        val beamElement = addBeam(0, 0,
                stemMaximum.xPosition.minus(stemMinimum.xPosition),
                -boundingBoxHeight.toInt(),
                // TODO This probably does not work now
                stemMinimum.boundingBox.width().toInt(),
                -height.toInt()
        )

        return PositionedRenderingElement(listOf(beamElement),
                findBoundingBox(beamElement.pathElements),
                "beam-${beamCounter++}",
                stemMinimum.xPosition,
                stemMinimum.yPosition)
    }

    fun findNote(elementId: String): NoteElement? {
        return noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .find { it.id.equals(elementId) }
    }



}