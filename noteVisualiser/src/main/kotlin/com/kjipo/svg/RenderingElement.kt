package com.kjipo.svg

import com.kjipo.font.PathInterface

data class RenderingElement(val renderingPath: List<PathInterface>) {

    constructor(renderingElement1: RenderingElement, renderingElement2: RenderingElement) : this(renderingElement1.renderingPath.plus(renderingElement2.renderingPath))


}