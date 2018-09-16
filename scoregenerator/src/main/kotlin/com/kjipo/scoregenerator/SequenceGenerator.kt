package com.kjipo.scoregenerator

import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreHandlerInterface
import java.util.*


import com.kjipo.score.*
import org.slf4j.LoggerFactory


class SequenceGenerator : ScoreHandlerInterface {
    private val random = Random()
    var scoreBuilder: ScoreBuilderImpl = ScoreBuilderImpl()
    var scoreHandler: ScoreHandler = ScoreHandler(scoreBuilder)
    val pitchSequence = mutableListOf<Pitch>()

    private val logger = LoggerFactory.getLogger(SequenceGenerator::class.java)


    fun createNewSequence(debug: Boolean = false): RenderingSequence {
        var timeCounter = 0
        pitchSequence.clear()

        scoreBuilder = ScoreBuilderImpl(debug)
        scoreHandler.scoreBuilder = scoreBuilder
        val bar = BAR(scoreBuilder)
        bar.clef = com.kjipo.score.Clef.G
        bar.timeSignature = TimeSignature(4, 4)

        val noteOrder = NoteType.values()
        var currentNote = NoteType.C
        var currentOctave = 5


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

            val note = NOTE(scoreBuilder)
            note.octave = currentOctave
            note.duration = duration
            note.note = currentNote

            val id = scoreBuilder.onNoteAdded(note)
            pitchSequence.add(Pitch(id, timeCounter, timeCounter + durationInMilliseconds, pitch, duration))
            timeCounter += durationInMilliseconds
        }

        scoreBuilder.onBarAdded(bar)

        return scoreBuilder.build()
    }


    private fun computeOnOffPitches() {
        var timeCounter = 0
        pitchSequence.forEach {
            it.timeOn = timeCounter
            it.timeOff = timeCounter + getDurationInMilliseconds(it.duration)
            timeCounter = it.timeOff
        }
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


    override fun moveNoteOneStep(id: String, up: Boolean) {
        pitchSequence
                .find { it.id == id }?.let { pitch ->

                    // TODO Only works because C major is the only key used so far
                    scoreBuilder.findNote(id)?.let { noteElement ->
                        val pitchStep = determinePitchStep(noteElement, up)
                        val index = pitchSequence.indexOf(pitch)
                        pitchSequence[index].pitch += pitchStep
                    }
                }
        scoreHandler.moveNoteOneStep(id, up)
    }


    override fun updateDuration(id: String, keyPressed: Int) {
        pitchSequence
                .find { it.id == id }?.let { pitch ->
                    scoreBuilder.findNote(id)?.let { noteElement ->
                        pitch.duration = when (keyPressed) {
                            1 -> Duration.QUARTER
                            2 -> Duration.HALF
                            3 -> Duration.WHOLE
                            else -> noteElement.duration
                        }
                    }
                    computeOnOffPitches()
                }
        scoreHandler.updateDuration(id, keyPressed)

    }


    private fun determinePitchStep(noteElement: NoteElement, up: Boolean): Int {
        return when (noteElement.note) {
            NoteType.A -> if (up) {
                2
            } else {
                -2
            }
            NoteType.H -> if (up) {
                1
            } else {
                -2
            }
            NoteType.C -> if (up) {
                2
            } else {
                -1
            }
            NoteType.D -> if (up) {
                2
            } else {
                -2
            }
            NoteType.E -> if (up) {
                1
            } else {
                -2
            }
            NoteType.F -> if (up) {
                2
            } else {
                -1
            }
            NoteType.G -> if (up) {
                2
            } else {
                -2
            }
        }
    }


    override fun getScoreAsJson() = scoreHandler.getScoreAsJson()


    override fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    override fun insertNote(activeElement: String, keyPressed: Int): String? {
        scoreHandler.insertNote(activeElement, keyPressed)?.let { idInsertedNote ->
            pitchSequence
                    .find { it.id == activeElement }?.let { pitch ->
                        scoreHandler.scoreBuilder.noteElements.find { it.id == idInsertedNote }?.let { temporalElement ->
                            if (temporalElement is NoteElement) {
                                pitchSequence.add(pitchSequence.indexOf(pitch) + 1, Pitch(idInsertedNote, 0, 0, getPitch(temporalElement.note, temporalElement.octave), temporalElement.duration))
                            }
                        }
                    }
            computeOnOffPitches()
            return idInsertedNote
        }
        return null
    }


    companion object {

        private val DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE = 1000

    }

}