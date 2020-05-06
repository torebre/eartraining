package com.kjipo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.kjipo.score.RenderingSequence

object RenderingSequenceJsonDiff {


    fun renderingDiff(renderingSequence: RenderingSequence, oldRenderingSequence: RenderingSequence): JsonNode {
        val objectMapper = ObjectMapper()
        val renderingSequenceNode = objectMapper.valueToTree<JsonNode>(renderingSequence)
        val oldRenderingSequenceNode = objectMapper.valueToTree<JsonNode>(oldRenderingSequence)

        return JsonDiff.asJson(oldRenderingSequenceNode, renderingSequenceNode)
    }



}