package com.kjipo

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import com.kjipo.handler.RenderingSequenceJsonDiff
import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Duration
import org.junit.Assert
import org.junit.Test

class RenderingSequenceJsonDiffTest {

    @Test
    fun `Diff is created based on rendering sequences`() {
        val scoreHandler = ScoreHandler()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence1 = scoreHandler.build()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence2 = scoreHandler.build()

        val objectMapper = ObjectMapper()
        val renderingDiff = RenderingSequenceJsonDiff.renderingDiff(renderingSequence2, renderingSequence1)

        val originalRenderingSequence = objectMapper.valueToTree<JsonNode>(renderingSequence1)
        val updatedSequenceAsJson = objectMapper.writeValueAsString(renderingSequence2)

        val updateJson = JsonPatch.apply(renderingDiff, originalRenderingSequence)
        val updatedJsonAsString = updateJson.toString()

        Assert.assertEquals(updatedSequenceAsJson, updatedJsonAsString)
    }

}