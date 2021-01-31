package com.kjipo.score

import com.kjipo.svg.findBoundingBox

class BeamElement(val id: String, private val start: Pair<Double, Double>, private val stop: Pair<Double, Double>, renderGroup: RenderGroup?) : ScoreRenderingElement(0, 0, renderGroup) {

    override fun toRenderingElement(): List<PositionedRenderingElement> {
        val beamElement = addBeam(
            start.first, start.second,
            stop.first, stop.second
        )

        return listOf(
            PositionedRenderingElement(
                listOf(beamElement),
                findBoundingBox(beamElement.pathElements),
                id
            )
        )
    }
}