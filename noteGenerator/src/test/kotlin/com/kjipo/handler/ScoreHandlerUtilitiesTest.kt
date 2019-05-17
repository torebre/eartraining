package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TICKS_PER_QUARTER_NOTE
import org.junit.Test
import org.assertj.core.api.Assertions.*


class ScoreHandlerUtilitiesTest {

    @Test
    fun splitRestsTest() {
        val totalTicksInBar = 4 * TICKS_PER_QUARTER_NOTE
        val rests = ScoreHandlerUtilities.splitIntoDurations(totalTicksInBar - TICKS_PER_QUARTER_NOTE)

        assertThat(rests).containsExactly(Duration.HALF, Duration.QUARTER)
    }

    @Test
    fun transformPitchToNoteAndOctave() {
        val (note, octave) = ScoreHandlerUtilities.pitchToNoteAndOctave(60)
        assertThat(note).isEqualTo(NoteType.C)
        assertThat(octave).isEqualTo(5)

        val (note2, octave2) = ScoreHandlerUtilities.pitchToNoteAndOctave(74)
        assertThat(note2).isEqualTo(NoteType.D)
        assertThat(octave2).isEqualTo(6)
    }

}