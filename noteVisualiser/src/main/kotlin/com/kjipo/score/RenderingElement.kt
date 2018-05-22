package com.kjipo.score

import com.kjipo.svg.BoundingBox
import com.kjipo.svg.GlyphData
import com.kjipo.svg.PathInterface


interface RenderingElement {
    val glyphData: GlyphData?
    val renderingPath: List<PathInterface>
    val boundingBox: BoundingBox
}

