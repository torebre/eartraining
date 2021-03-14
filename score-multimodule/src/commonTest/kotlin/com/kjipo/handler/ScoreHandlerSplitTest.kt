package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import kotlin.test.Test
import kotlin.test.assertTrue


class ScoreHandlerSplitTest {

    @Test
    fun `Score is populated`() {
        val scoreHandlerSplit = ScoreHandlerSplit()
        scoreHandlerSplit.insertNote(Duration.QUARTER, 5, NoteType.C)
        val scoreAsJson = scoreHandlerSplit.getScoreAsJson()

        // Simple check that there is a quarter note element in the output
        assertTrue { scoreAsJson.contains("QUARTER") }
    }

}