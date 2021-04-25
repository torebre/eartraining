package com.kjipo.score

import com.kjipo.handler.Bar
import com.kjipo.handler.Note
import com.kjipo.handler.Score
import com.kjipo.handler.ScoreHandlerWithReducedLogic
import org.junit.Test

class ScoreSetupTest {

    @Test
    fun testCreateScore() {
        val bar = Bar().also {
            it.clef = Clef.G
            it.timeSignature = TimeSignature(4, 4)
        }

        val note1 = Note("test1", Duration.QUARTER, 5, NoteType.A)
        val note2 = Note("test2", Duration.QUARTER, 5, NoteType.A)
        val notes = listOf(note1, note2)

        bar.scoreHandlerElements.addAll(notes)

        val score = Score()
        score.bars.add(bar)

        score.ties.add(Pair(note1, note2))

        val scoreHandler = ScoreHandlerWithReducedLogic(score)

        println(scoreHandler.getScoreAsJson())

    }

}