package com.kjipo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonDiff
import com.flipkart.zjsonpatch.JsonPatch
import com.kjipo.handler.ScoreHandlerSplit
import com.kjipo.handler.ScoreHandlerWithReducedLogic
import com.kjipo.score.Duration
import com.kjipo.score.RenderingSequenceWithMetaData
import org.junit.Assert
import org.junit.Test

class RenderingSequenceJsonDiffTest {

    @Test
    fun `Diff is created based on rendering sequences`() {
        val scoreHandler = ScoreHandlerSplit()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence1 = scoreHandler.build()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence2 = scoreHandler.build()

        val objectMapper = ObjectMapper()
        val renderingDiff = renderingDiff(renderingSequence2, renderingSequence1)

        val originalRenderingSequence = objectMapper.valueToTree<JsonNode>(renderingSequence1)
        val updatedSequenceAsJson = objectMapper.writeValueAsString(renderingSequence2)

        val updateJson = JsonPatch.apply(renderingDiff, originalRenderingSequence)
        val updatedJsonAsString = updateJson.toString()

        Assert.assertEquals(updatedSequenceAsJson, updatedJsonAsString)
    }

    private fun renderingDiff(renderingSequence: RenderingSequenceWithMetaData, oldRenderingSequence: RenderingSequenceWithMetaData): JsonNode {
        val objectMapper = ObjectMapper()

        val renderingSequenceNode = objectMapper.valueToTree<JsonNode>(renderingSequence)
        val oldRenderingSequenceNode = objectMapper.valueToTree<JsonNode>(oldRenderingSequence)

        return JsonDiff.asJson(oldRenderingSequenceNode, renderingSequenceNode)
    }

}