package com.kjipo.svg

import com.kjipo.font.BoundingBox
import com.kjipo.font.GlyphData
import com.kjipo.font.PathInterface


interface RenderingElement {
    val glyphData: GlyphData?
    val renderingPath: List<PathInterface>
    val boundingBox: BoundingBox
}

