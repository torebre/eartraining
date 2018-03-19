package com.kjipo.font

class GlyphData(val name: String, override val pathElements: List<PathElement>, override val strokeWidth: Int, val boundingBox: BoundingBox) : PathInterface {


    constructor(name: String, pathElements: List<PathElement>, boundingBox: BoundingBox) : this(name, pathElements, 1, boundingBox)

    override fun toString(): String {
        return "GlyphData{" +
                "name='" + name + '\''.toString() +
                ", pathElements=" + pathElements +
                ", strokeWidth=" + strokeWidth +
                ", boundingBox=" + boundingBox +
                '}'.toString()
    }
}
