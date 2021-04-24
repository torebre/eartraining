package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import kotlin.test.Test

class ScoreElementsTranslatorTest {


    @Test
    fun testTranslation() {
        val scoreHandlerElements = listOf(
            NoteSequenceElement.NoteElement("1", NoteType.C, 5, Duration.QUARTER, emptyMap()),
            NoteSequenceElement.NoteElement("2", NoteType.C, 5, Duration.QUARTER, emptyMap())
        )

        val score = ScoreElementsTranslator.createRenderingData(scoreHandlerElements)

        println("Score: $score")
    }

}