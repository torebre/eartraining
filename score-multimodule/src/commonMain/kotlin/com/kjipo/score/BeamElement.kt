package com.kjipo.score

import com.kjipo.svg.findBoundingBox

class BeamElement(
    val id: String,
    private val start: Pair<Double, Double>,
    private val stop: Pair<Double, Double>
) : ScoreRenderingElement(0, 0) {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val beamElement = addBeam(
            start.first, start.second,
            stop.first, stop.second
        )

        // TODO Should this be a translated or absolutely positioned element?
        return listOf(
            TranslatedRenderingElement(
                listOf(beamElement),
                findBoundingBox(beamElement.pathElements),
                id,
                null,
                Translation(0, 0)
            )
        )
    }
}