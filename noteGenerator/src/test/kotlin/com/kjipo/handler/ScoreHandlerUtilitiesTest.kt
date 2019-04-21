package com.kjipo.handler

import com.kjipo.score.Duration
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

}