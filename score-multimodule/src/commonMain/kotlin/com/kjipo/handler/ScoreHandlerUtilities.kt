package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TICKS_PER_QUARTER_NOTE
import mu.KotlinLogging
import kotlin.math.absoluteValue

object ScoreHandlerUtilities {
    const val DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE = 1000

    private val logger = KotlinLogging.logger {}

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

        logger.debug { "Note type: $noteType" }

        return 12 * octave + when (noteType) {
            NoteType.A -> 9
            NoteType.A_SHARP -> 10
            NoteType.H -> 11
            NoteType.C -> 0
            NoteType.C_SHARP -> 1
            NoteType.D -> 2
            NoteType.D_SHARP -> 3
            NoteType.E -> 4
            NoteType.F -> 5
            NoteType.F_SHARP -> 6
            NoteType.G -> 7
            NoteType.G_SHARP -> 8
        }
    }


    fun getDurationInMilliseconds(duration: Duration): Int {
        return when (duration) {
            Duration.ZERO -> 0
            Duration.EIGHT -> DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE / 2
            Duration.HALF -> 2 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            Duration.QUARTER -> DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
            Duration.WHOLE -> 4 * DEFAULT_TEMPO_MILLISECONDS_PER_QUARTER_NOTE
        }
    }

    fun pitchToNoteAndOctave(pitch: Int): Pair<NoteType, Int> {
        val remainder = pitch.rem(12)
        val noteType = when (remainder) {
            9 -> NoteType.A
            10 -> NoteType.A_SHARP
            11 -> NoteType.H
            0 -> NoteType.C
            1 -> NoteType.C_SHARP
            2 -> NoteType.D
            3 -> NoteType.D_SHARP
            4 -> NoteType.E
            5 -> NoteType.F
            6 -> NoteType.F_SHARP
            7 -> NoteType.G
            8 -> NoteType.G_SHARP
            else -> throw IllegalArgumentException("Unhandled pitch: $pitch")
        }

        return Pair(noteType, pitch.minus(remainder).div(12))
    }


}