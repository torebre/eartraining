package com.kjipo.score

import com.kjipo.handler.*
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


    @Test
    fun testHighlightForNoteGroup() {
        val bar = Bar().also {
            it.clef = Clef.G
            it.timeSignature = TimeSignature(4, 4)
        }

        val note1 = NoteSymbol("test1", Duration.QUARTER, 5, NoteType.A)
        val note2 = NoteSymbol("test2", Duration.QUARTER, 5, NoteType.C)

        val noteGroup = NoteGroup("testGroup", listOf(note1, note2)) //, mapOf(Pair("elementId", "testGroup")))

        bar.scoreHandlerElements.addAll(listOf(noteGroup))

        val score = Score()
        score.bars.add(bar)

        val scoreHandler = ScoreHandlerWithReducedLogic(score)

        println("Highlight elements: ${scoreHandler.getHighlightMap()}")

    }

}