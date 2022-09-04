package com.kjipo.score

import kotlinx.serialization.Serializable


@Serializable
data class RenderingSequenceUpdate(
    val renderGroupUpdates: Map<String, PositionedRenderingElementParent>,
    val viewBox: ViewBox?
) {
    // TODO Also include updates to definitions


}