package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteElement
import com.kjipo.score.NoteType
import com.kjipo.score.TICKS_PER_QUARTER_NOTE
import kotlin.math.absoluteValue

object ScoreHandlerUtilities {
    const val DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE = 1000

    // TODO Only works for the C-scale
    fun determinePitchStep(noteElement: NoteElement, up: Boolean): Int {
        return ScoreHandlerUtilities.determinePitchStep(noteElement, up)
    }

    fun determinePitchStep(noteType: NoteType, up: Boolean): Int {
        return when (noteType) {
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

    fun getDuration(keyPressed: Int): Duration =
            when (keyPressed) {
                1 -> Duration.QUARTER
                2 -> Duration.HALF
                3 -> Duration.WHOLE
                else -> Duration.QUARTER
            }


    fun getDurationForTicks(ticks: Int): Duration {
        return when (ticks) {
            TICKS_PER_QUARTER_NOTE -> Duration.QUARTER
            2 * TICKS_PER_QUARTER_NOTE -> Duration.HALF
            4 * TICKS_PER_QUARTER_NOTE -> Duration.WHOLE
            else -> throw IllegalArgumentException("Unhandled number of ticks: $ticks")
        }
    }

    fun splitDuration(ticks: Int, ticksLeftInBar: Int): List<Duration> {
        if (ticks < ticksLeftInBar) {
            return listOf(getDurationForTicks(ticks))
        }
        val remainder = ticksLeftInBar.minus(ticks).absoluteValue
        return listOf(getDurationForTicks(ticksLeftInBar)) + splitIntoDurations(remainder)
    }

    fun splitIntoDurations(ticks: Int): List<Duration> {
        if (ticks == 0) {
            return emptyList()
        }
        var ticksCounter = ticks
        val result = mutableListOf<Duration>()

        while (ticksCounter != 0) {
            Duration.values().last { it.ticks <= ticksCounter }.let {
                result.add(it)
                ticksCounter -= it.ticks
            }
        }
        return result
    }




        fun getPitch(noteType: NoteType, octave: Int): Int {
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


        fun getDurationInMilliseconds(duration: Duration): Int {
            return when (duration) {
                Duration.ZERO -> 0
                Duration.HALF -> 2 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
                Duration.QUARTER -> DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
                Duration.WHOLE -> 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            }
        }



    }