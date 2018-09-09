package com.kjipo.scoregenerator

import java.util.*


import com.kjipo.score.*
import org.slf4j.LoggerFactory


class SequenceGenerator {
    private val random = Random()
    var scoreBuilder: ScoreBuilderImpl = ScoreBuilderImpl()
    val pitchSequence = mutableListOf<Pitch>()

    private val logger = LoggerFactory.getLogger(SequenceGenerator::class.java)


    fun createNewSequence(debug: Boolean = false): RenderingSequence {
        var timeCounter = 0
        pitchSequence.clear()

        scoreBuilder = ScoreBuilderImpl(debug)
        var bar = BAR(scoreBuilder)
        bar.clef = com.kjipo.score.Clef.G
        bar.timeSignature = TimeSignature(4, 4)

        val noteOrder = NoteType.values()
        var currentNote = NoteType.C
        var currentOctave = 5
        var idCounter = 0

        for (i in 0..4) {
            var currentIndex = noteOrder.indexOf(currentNote)
            val step = random.nextInt(0..3)
            val stepUp = random.nextBoolean()

            if (stepUp) {
                if (currentNote == NoteType.H) {
                    currentNote = NoteType.C
                    ++currentOctave
                } else {
                    currentNote = NoteType.values()[(currentNote.ordinal + 1) % NoteType.values().size]
                }
            } else {
                if (currentNote == NoteType.C) {
                    currentNote = NoteType.H
                    --currentOctave
                } else {
                    currentNote = NoteType.values()[(NoteType.values().size + currentNote.ordinal - 1) % NoteType.values().size]
                }
            }

            val pitch = getPitch(currentNote, currentOctave)
            val duration = getDuration()
            val durationInMilliseconds = getDurationInMilliseconds(duration)
            val idToUse = "note-" + (idCounter++)

            val note = NOTE(scoreBuilder)
            note.octave = currentOctave
            note.id = idToUse
            note.duration = duration
            note.note = currentNote

            scoreBuilder.onNoteAdded(note)
            pitchSequence.add(Pitch(idToUse, timeCounter, timeCounter + durationInMilliseconds, pitch))
            timeCounter += durationInMilliseconds
        }

        scoreBuilder.onBarAdded(bar)
        return scoreBuilder.build()
    }

    private fun Random.nextInt(range: IntRange): Int {
        return range.start + nextInt(range.last - range.start)
    }


    private fun getPitch(noteType: NoteType, octave: Int): Int {
        return 12 * octave + when (noteType) {
            NoteType.A -> 9
            NoteType.H -> 11
            NoteType.C -> 0
            NoteType.D -> 2
            NoteType.E -> 4
            NoteType.F -> 5
            NoteType.G -> 7
        }
    }

    private fun getDuration(): Duration {
        return if (Math.random() < 0.3) {
            Duration.HALF
        } else {
            Duration.QUARTER
        }
    }

    private fun getDurationInMilliseconds(duration: Duration): Int {
        return when (duration) {
            Duration.HALF -> 2 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            Duration.QUARTER -> DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            Duration.WHOLE -> 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
        }
    }

    companion object {

        private val DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE = 1000

    }

}