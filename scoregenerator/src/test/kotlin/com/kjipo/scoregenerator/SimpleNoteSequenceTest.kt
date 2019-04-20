package com.kjipo.scoregenerator

import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Test


class SimpleNoteSequenceTest {


    @Test
    fun `Transform to pitch sequence test`() {
        val simpleNoteSequence = SimpleNoteSequence(listOf(NoteSequenceElement.NoteElement(NoteType.A, 4, Duration.QUARTER),
                NoteSequenceElement.NoteElement(NoteType.H, 4, Duration.QUARTER),
                NoteSequenceElement.NoteElement(NoteType.C, 5, Duration.QUARTER)))
        val pitchSequence = simpleNoteSequence.transformToPitchSequence()

        assertThat(pitchSequence.size, IsEqual(3))
        assertThat(pitchSequence[0], IsEqual(Pitch("0", 0, Utilities.DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE, 57, Duration.QUARTER)))
        assertThat(pitchSequence[1], IsEqual(Pitch("1", Utilities.DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE, 2 * Utilities.DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE, 59, Duration.QUARTER)))
        assertThat(pitchSequence[2], IsEqual(Pitch("2", 2 * Utilities.DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE, 3 * Utilities.DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE, 60, Duration.QUARTER)))
    }


}