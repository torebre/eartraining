package com.kjipo.svg

import com.kjipo.font.PathInterface

data class RenderingElementImpl(override val renderingPath: List<PathInterface>) : RenderingElement {

    constructor(renderingElement1: RenderingElementImpl, renderingElement2: RenderingElementImpl) : this(renderingElement1.renderingPath.plus(renderingElement2.renderingPath))


}