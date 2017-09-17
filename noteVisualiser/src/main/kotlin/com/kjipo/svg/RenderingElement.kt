package com.kjipo.svg

import com.kjipo.font.BoundingBox
import com.kjipo.font.PathInterface


interface RenderingElement {
    val renderingPath: List<PathInterface>
    val boundingBox: BoundingBox

//    val beamGroup: Int
//    val stem: StemElement?
}

