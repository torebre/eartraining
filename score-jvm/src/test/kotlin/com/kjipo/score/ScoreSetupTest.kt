package com.kjipo.score

import org.junit.Test

class ScoreSetupTest {


    @Test
    fun `Add accidental test`() {
        val scoreData = ScoreSetup()
        var idCounter = 0

        val note1 = NoteElement(NoteType.C, 5, Duration.HALF, 0, 0, "note-$idCounter")
        note1.accidental = Accidental.SHARP
        ++idCounter
        val note2 = NoteElement(NoteType.D, 5, Duration.HALF, 0, 0, "note-$idCounter")
        note2.tie = note1.id
        note2.accidental = Accidental.SHARP
        ++idCounter

        scoreData.noteElements.add(note1)
        scoreData.noteElements.add(note2)

        val barData = BarData()
        barData.clef = Clef.G
        barData.scoreRenderingElements.add(note1)
        barData.scoreRenderingElements.add(note2)

        scoreData.bars.add(barData)

        val scoreBuilderSequence = ScoreBuilderSequence(scoreData)

        scoreBuilderSequence.getScoreAsJson()
    }


}