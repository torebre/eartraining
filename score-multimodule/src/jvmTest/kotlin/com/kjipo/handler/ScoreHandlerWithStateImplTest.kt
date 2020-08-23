package com.kjipo.handler

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.flipkart.zjsonpatch.JsonPatch
import com.kjipo.score.Duration
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test

class ScoreHandlerWithStateImplTest {

    @Test
    fun `Insert note operation generates score diff`() {
        val scoreHandler = ScoreHandler()

        val scoreHandlerWithState = ScoreHandlerWithStateImpl(scoreHandler)

        val diff = scoreHandlerWithState.applyOperation(InsertNote(null, duration = Duration.QUARTER))

        assertNotNull(diff)


//        scoreHandler.insertNote(Duration.QUARTER)
//        val renderingSequence1 = scoreHandler.build()
//
//        scoreHandler.insertNote(Duration.QUARTER)
//        val renderingSequence2 = scoreHandler.build()
//
//
//
//
//        val objectMapper = ObjectMapper()
//        val renderingDiff = RenderingSequenceJsonDiff.renderingDiff(renderingSequence2, renderingSequence1)
//
//        val originalRenderingSequence = objectMapper.valueToTree<JsonNode>(renderingSequence1)
//        val updatedSequenceAsJson = objectMapper.writeValueAsString(renderingSequence2)
//
//        val updateJson = JsonPatch.apply(renderingDiff, originalRenderingSequence)
//        val updatedJsonAsString = updateJson.toString()

    }


}