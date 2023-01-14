package com.kjipo.score

import com.kjipo.handler.*
import com.kjipo.svg.EIGHT_NOTE_FLAG_DOWN
import com.kjipo.svg.EIGHT_NOTE_FLAG_UP
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.Test


class NoteFlagTest {

    @Test
    fun noteFlagTest() {
        val score = Score()
        val bar = Bar(clef = Clef.G, timeSignature = TimeSignature(4, 4))

        val note31 = Note("test31", Duration.EIGHT, 6, NoteType.F, stem = Stem.DOWN)
        val note32 = Note("test32", Duration.EIGHT, 5, NoteType.E, stem = Stem.UP)
        val note33 = Note("test33", Duration.EIGHT, 6, NoteType.F_SHARP, stem = Stem.UP)
        val note34 = Note("test34", Duration.EIGHT, 5, NoteType.G, stem = Stem.DOWN)
        val rest3 = Rest("test35", Duration.HALF)

        bar.scoreHandlerElements.addAll(listOf(note31, note32, note33, note34, rest3))
        score.bars.add(bar)

        val scoreSetup = ScoreSetup(score)
        scoreSetup.buildWithMetaData()
        val noteIds = setOf("test31", "test32", "test33", "test34")

        // There are no beam groups defined so all notes here
        // should have a note flag
        scoreSetup.scoreRenderingElements.filter { it is NoteElement && it.id in noteIds }
            .forEach {
                if (it is NoteElement) {
                    if (it.isStemUp()) {
                        assertThat(it.getGlyphs(), hasKey(EIGHT_NOTE_FLAG_UP))
                    } else {
                        assertThat(it.getGlyphs(), hasKey(EIGHT_NOTE_FLAG_DOWN))
                    }
                }
            }
    }

    @Test
    fun `No note flag when notes are part of beam group`() {
        val score = Score()
        val bar = Bar(clef = Clef.G, timeSignature = TimeSignature(4, 4))

        val note31 = Note("test31", Duration.EIGHT, 6, NoteType.F, stem = Stem.DOWN)
        val note32 = Note("test32", Duration.EIGHT, 5, NoteType.E, stem = Stem.UP)
        val note33 = Note("test33", Duration.EIGHT, 6, NoteType.F_SHARP, stem = Stem.UP)
        val note34 = Note("test34", Duration.EIGHT, 5, NoteType.G, stem = Stem.DOWN)
        val rest3 = Rest("test35", Duration.HALF)

        bar.scoreHandlerElements.addAll(listOf(note31, note32, note33, note34, rest3))
        score.bars.add(bar)

        val beamGroup = BeamGroup(listOf(BeamLine(1, listOf(note31, note34))))
        score.beamGroups.add(beamGroup)
        val beamGroup2 = BeamGroup(listOf(BeamLine(1, listOf(note32, note33))))
        score.beamGroups.add(beamGroup2)

        val scoreSetup = ScoreSetup(score)
        scoreSetup.buildWithMetaData()
        val noteIds = setOf("test31", "test32", "test33", "test34")

        // All the notes here are a part of a beam group so there should be
        // no flags attached to any of the notes
        scoreSetup.scoreRenderingElements.filter { it is NoteElement && it.id in noteIds }
            .forEach {
                if (it is NoteElement) {
                    assertThat(it.getGlyphs(), not(hasKey(EIGHT_NOTE_FLAG_UP)))
                    assertThat(it.getGlyphs(), not(hasKey(EIGHT_NOTE_FLAG_DOWN)))
                }
            }


    }


}