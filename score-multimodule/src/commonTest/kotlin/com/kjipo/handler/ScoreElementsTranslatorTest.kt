package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import kotlin.test.Test

class ScoreElementsTranslatorTest {


    @Test
    fun testTranslation() {
        val scoreHandlerElements = listOf(
            NoteOrRest("1", Duration.QUARTER, true, 5, NoteType.C),
            NoteOrRest("2", Duration.QUARTER, true, 5, NoteType.C)
        )

        val score = ScoreElementsTranslator.createRenderingData(scoreHandlerElements)

        println("Score: $score")


    }


}