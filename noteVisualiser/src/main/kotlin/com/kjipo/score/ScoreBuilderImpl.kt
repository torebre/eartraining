package com.kjipo.score

import com.kjipo.svg.findBoundingBox


class ScoreBuilderImpl(override val debug: Boolean = false) : ScoreBuilderInterface<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    private val noteElements = mutableListOf<TemporalElement>()
    private val bars = mutableListOf<BAR>()


    override fun onBarAdded(bar: BAR) {
        // TODO Figure out best way to set the value
        bar.scoreRenderingElements.addAll(currentElements)
        currentElements.clear()
        if (bars.isNotEmpty()) {
            // TODO Is this the correct direction? In which direction are the bars added through the method calls?
            bar.previousBar = bars.last()
            bar.scoreRenderingElements.filter { it is NoteElement }.map { it as NoteElement }.forEach { it.bar = bar }
        }
        bars.add(bar)
    }

    override fun onNoteAdded(note: NOTE) {
        val noteElement = NoteElement(note.note, note.octave, note.duration, 0, calculateVerticalOffset(note.note, note.octave), note.beamGroup)

        currentElements.add(noteElement)
        noteElements.add(noteElement)
    }

    override fun onRestAdded(rest: REST) {
        val restElement = RestElement(rest.duration, 0, 0)

        currentElements.add(restElement)
        noteElements.add(restElement)
    }

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingElements = mutableListOf<PositionedRenderingElement>()

        var barXoffset = 0
        var barYoffset = 0

        val barXspace = 0
        val barYspace = 200

        bars.forEach {
            renderingElements.addAll(it.build(barXoffset, barYoffset))
            barXoffset += barXspace
            barYoffset += barYspace
        }

        val beamGroups = mutableMapOf<Int, MutableCollection<StemElement>>()

        noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .forEach {
            if (it.requiresStem()) {
                val stem = addStem(it.toRenderingElement().boundingBox)
                val stemElement = StemElement(it.xPosition, it.yPosition, listOf(stem), findBoundingBox(stem.pathElements), it)

                beamGroups.compute(it.beamGroup, { beamGroup, stemElements ->
                    if (stemElements == null) {
                        mutableListOf(stemElement)
                    } else {
                        stemElements.add(stemElement)
                        stemElements
                    }
                })
            }
        }

        beamGroups.forEach({ beamGroup, stemElements ->
            if (beamGroup == 0) {
                renderingElements.addAll(stemElements)
            } else {
                renderingElements.addAll(stemElements)
                renderingElements.add(handleBeams(stemElements))
            }
        })

        // TODO Set proper view box
        return RenderingSequence(renderingElements, ViewBox(0, 0, 2000, 2000))
    }


    private fun handleBeams(beamGroup: Collection<StemElement>): BeamElement {
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
                stemMinimum.noteElement.toRenderingElement().boundingBox.width().toInt(),
                -height.toInt()
        )


        return BeamElement(stemMinimum.xPosition,
                stemMinimum.yPosition,
                listOf(beamElement),
                findBoundingBox(beamElement.pathElements))
    }

}