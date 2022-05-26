package com.kjipo.score

import com.kjipo.handler.*
import org.junit.Test

class TieAcrossBarsTest {

    @Test
    fun testTieAcrossBars() {
        val score = Score()

        val bar = Bar().apply {
            clef = Clef.G
            timeSignature = TimeSignature(4, 4)
        }

        val note1 = Note("test1", Duration.HALF, 5, NoteType.A, stem = Stem.UP)
        val rest1 = Rest("rest1", Duration.HALF)
        bar.scoreHandlerElements.addAll(listOf(rest1, note1))
        score.bars.add(bar)

        val bar2 = Bar()
        val note2 = Note("test2", Duration.WHOLE, 5, NoteType.A)
        bar2.scoreHandlerElements.addAll(listOf(note2))
        score.bars.add(bar2)

        val scoreHandler = ScoreHandlerWithReducedLogic(score)
        score.ties.add(Pair(note1, note2))

        scoreHandler.getScoreAsJson()
    }

}