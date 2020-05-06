package com.kjipo

import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Duration
import org.junit.Test

class RenderingSequenceJsonDiffTest {

    @Test
    fun `Diff is created based on rendering sequences`() {
        val scoreHandler = ScoreHandler()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence1 = scoreHandler.build()

        scoreHandler.insertNote(Duration.QUARTER)
        val renderingSequence2 = scoreHandler.build()

        val renderingDiff = RenderingSequenceJsonDiff.renderingDiff(renderingSequence1, renderingSequence2)

        println(renderingDiff.toPrettyString())


    }



}