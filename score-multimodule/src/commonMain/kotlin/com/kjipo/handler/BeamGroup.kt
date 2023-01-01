package com.kjipo.handler



class BeamLine(val position: Int, val elements: List<IsBeamableElement>)

data class BeamGroup(val beamLines: List<BeamLine>)
