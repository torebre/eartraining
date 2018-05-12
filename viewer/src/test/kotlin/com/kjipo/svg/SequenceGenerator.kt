package com.kjipo.svg

import org.slf4j.LoggerFactory
import java.util.*

class SequenceGenerator {
    private val random = Random()

    private val noteStep = mapOf(
            Pair(NoteType.A, 2),
            Pair(NoteType.H, 2),
            Pair(NoteType.C, 1),
            Pair(NoteType.D, 2),
            Pair(NoteType.E, 2),
            Pair(NoteType.F, 1),
            Pair(NoteType.G, 2)
    )


    private val logger = LoggerFactory.getLogger(SequenceGenerator::class.java)


    fun createNewSequence(debug: Boolean = false): RenderingSequence {

        var id = 0
        var timeCounter = 0
        var currentBarEnd = 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
        var beatCounter = 0
        val random = Random()

        // TODO This only works because there are only two types of notes used so far

        val scoreBuilder = ScoreBuilderImpl(debug)
        var bar = BAR(scoreBuilder)

        bar.clef = Clef.G
        bar.timeSignature = TimeSignature(4, 4)

        val noteOrder = NoteType.values()
        var currentNote = NoteType.C
        var previousPitch = 60

        var beamGroupCounter = 1

        for (i in 0..4) {
            var currentIndex = noteOrder.indexOf(currentNote)
            val step = random.nextInt(0..3)
            val stepDown = random.nextBoolean()
            var nextPitch = previousPitch

            for (j in 0 until step) {
                if (stepDown) {
                    nextPitch -= noteStep.getOrElse(noteOrder[currentIndex], {
                        logger.error("Note not found: $nextPitch")
                    0})
                    --currentIndex
                    if (currentIndex == -1) {
                        currentIndex = noteOrder.size - 1
                    }
                } else {
                    ++currentIndex
                    if (currentIndex == noteOrder.size) {
                        currentIndex = 0
                    }
                    nextPitch += noteStep.getOrElse(noteOrder[currentIndex], {
                        logger.error("Note not found: $nextPitch")
                        0
                    })
                }
            }

            currentNote = noteOrder[currentIndex]

            val duration2 = if (Math.random() < 0.3) {
                Duration.HALF
            } else {
                Duration.QUARTER
            }

            val durationMilliSeconds = when (duration2) {
                Duration.HALF -> 2 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
                Duration.QUARTER -> DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
                Duration.WHOLE -> 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            }


            val currentOctave = nextPitch.div(12)

            logger.info("Next pitch: $nextPitch. Next pitch: $nextPitch. Current octave: $currentOctave")


            if (timeCounter < currentBarEnd && timeCounter + durationMilliSeconds > currentBarEnd) {
                val beamGroup2 = beamGroupCounter++
                val firstNote = NOTE(scoreBuilder)

                firstNote.note = currentNote
                firstNote.duration = duration2
                firstNote.beamGroup = beamGroup2
                firstNote.octave = currentOctave

                scoreBuilder.onNoteAdded(firstNote)

                // New bar
                beatCounter = 1
                currentBarEnd += 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE

                scoreBuilder.onBarAdded(bar)

                bar = BAR(scoreBuilder)

                val note = NOTE(scoreBuilder)
                note.note = currentNote
                note.duration = duration2
                note.beamGroup = beamGroup2
                note.octave = currentOctave

                scoreBuilder.onNoteAdded(note)
            } else {
                val note = NOTE(scoreBuilder)
                note.note = currentNote
                note.duration = duration2
                note.octave = currentOctave

                scoreBuilder.onNoteAdded(note)
            }



            beatCounter += when (duration2) {
                Duration.QUARTER -> 1
                Duration.HALF -> 2
                Duration.WHOLE -> 4
            }
            beatCounter %= 4
            timeCounter += durationMilliSeconds
            previousPitch = nextPitch
        }

        scoreBuilder.onBarAdded(bar)

        return scoreBuilder.build()
    }

    private fun Random.nextInt(range: IntRange): Int {
        return range.start + nextInt(range.last - range.start)
    }

    companion object {

        private val DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE = 1000

    }

}