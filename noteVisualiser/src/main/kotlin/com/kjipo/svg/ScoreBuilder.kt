package com.kjipo.svg

import com.kjipo.font.findBoundingBox
import com.kjipo.font.height
import com.kjipo.font.width

class ScoreBuilder : ElementConsumer<RenderingSequence> {
    private val currentElements = mutableListOf<ScoreRenderingElement>()
    private val noteElements = mutableListOf<NoteElement>()
    private val bars = mutableListOf<BAR>()

    private var counter = 0


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
        // TODO Set proper location
        val noteElement = NoteElement(note.note, note.octave, note.duration, counter, calculateVerticalOffset(note.note, note.octave), note.beamGroup)

        currentElements.add(noteElement)
        noteElements.add(noteElement)
        // TODO Set correct counter value that takes into account multiple measures
        counter += note.duration
    }

    fun score(init: SCORE.() -> Unit) = SCORE(this).apply(init).finalize(this)

    override fun build(): RenderingSequence {
        // TODO Possible to use immutable lists here?
        // TODO The position will be wrong when there are multiple bars
        val renderingElements = mutableListOf<PositionedRenderingElement>()
        bars.forEach {
            renderingElements.addAll(it.build())
        }

        val beamGroups = mutableMapOf<Int, MutableCollection<StemElement>>()

        // TODO Comment back in
//        noteElements.forEach {
//            if (it.requiresStem()) {
//                val stem = addStem(it.toRenderingElement().boundingBox)
//                val stemElement = StemElement(it.xPosition, it.yPosition, listOf(stem), findBoundingBox(stem.pathElements), it)
//
//                beamGroups.compute(it.beamGroup, { beamGroup, stemElements ->
//                    if (stemElements == null) {
//                        mutableListOf(stemElement)
//                    } else {
//                        stemElements.add(stemElement)
//                        stemElements
//                    }
//                })
//
//            }
//
//        }

        // TODO Comment back in
//        beamGroups.forEach({ beamGroup, stemElements ->
//            if (beamGroup == 0) {
//                renderingElements.addAll(stemElements)
//            } else {
//                renderingElements.addAll(stemElements)
//                renderingElements.add(handleBeams(stemElements))
//            }
//        })

        return RenderingSequence(renderingElements)
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