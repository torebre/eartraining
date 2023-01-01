package com.kjipo.score

import com.kjipo.handler.*
import org.junit.Test


class BeamTest {


    @Test
    fun beamTest() {
        val bar = Bar(clef = Clef.G, timeSignature = TimeSignature(4, 4))

        val note1 = Note("test1", Duration.QUARTER, 5, NoteType.A_SHARP, stem = Stem.UP)
        val note2 = Note("test2", Duration.QUARTER, 5, NoteType.A, stem = Stem.UP)
        val rest1 = Rest("test3", Duration.HALF)
        val notes = listOf(note1, note2, rest1)

        bar.scoreHandlerElements.addAll(notes)

        val score = Score()
        score.bars.add(bar)

        val bar2 = Bar()

        val note3 = Note("test4", Duration.EIGHT, 5, NoteType.F, stem = Stem.DOWN)
        val note4 = Note("test5", Duration.EIGHT, 5, NoteType.E, stem = Stem.DOWN)
        val note5 = Note("test6", Duration.EIGHT, 5, NoteType.F, stem = Stem.DOWN)
        val note6 = Note("test7", Duration.EIGHT, 5, NoteType.G, stem = Stem.DOWN)
        val rest2 = Rest("test8", Duration.HALF)

        bar2.scoreHandlerElements.addAll(listOf(note3, note4, note5, note6, rest2))
        score.bars.add(bar2)

        val beamGroup = BeamGroup(listOf(BeamLine(1, listOf(note1, note2))))
        score.beamGroups.add(beamGroup)

        val beamGroup2 = BeamGroup(listOf(BeamLine(1, listOf(note3, note4, note5, note6))))
        score.beamGroups.add(beamGroup2)

        val scoreHandler = ScoreHandlerWithReducedLogic(score)

        scoreHandler.getScoreAsJson()

    }

}