package com.kjipo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.kjipo.score.RenderingSequence
import kotlinx.serialization.json.Json

object RenderingSequenceJsonDiff {


    fun renderingDiff(renderingSequence: RenderingSequence, oldRenderingSequence: RenderingSequence) {
        Json.stringify(RenderingSequence.serializer(), renderingSequence)

        val objectMapper = ObjectMapper()
        val renderingSequenceNode = objectMapper.valueToTree<JsonNode>(renderingSequence)
        val oldRenderingSequenceNode = objectMapper.valueToTree<JsonNode>(oldRenderingSequence)


    }



}