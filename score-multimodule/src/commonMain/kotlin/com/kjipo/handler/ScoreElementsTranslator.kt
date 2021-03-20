package com.kjipo.handler

import com.kjipo.score.*
import mu.KotlinLogging

object ScoreElementsTranslator {

    private val trimEndBars = true

    private val ticksPerBar = 4 * TICKS_PER_QUARTER_NOTE

    private val logger = KotlinLogging.logger {}


    fun createRenderingData(noteSequenceElements: List<NoteSequenceElement>): Score {
        val score = Score()

        var remainingTicksInBar = ticksPerBar
        var currentBar = Bar(score)
        currentBar.clef = Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)
        score.bars.add(currentBar)

        // TODO The highlights do not work properly, one note sequence elements should be able to light up several elements in the score
        for (element in noteSequenceElements) {
            when (element) {
                is NoteSequenceElement.NoteElement, is NoteSequenceElement.RestElement -> {
                    val ticksNeededForElement = element.duration.ticks

                    val (octave, note, isNote) = if (element is NoteSequenceElement.NoteElement) {
                        Triple(element.octave, element.note, true)
                    } else {
                        Triple(5, NoteType.C, false)
                    }

                    if (remainingTicksInBar == 0) {
                        // No more room in bar, start on a new one
                        remainingTicksInBar = ticksPerBar
                        currentBar = Bar(score)
                        score.bars.add(currentBar)
                    }

                    when {
                        remainingTicksInBar < ticksNeededForElement -> {
                            val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar)
                            val durationsInNextBar =
                                ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar)

                            var previous: ScoreHandlerElement? = null
                            for (duration in durationsInCurrentBar) {
                                val splitScoreElement =
                                    NoteOrRest(element.id, duration, isNote, octave, note)

                                if (previous != null) {
                                    score.ties.add(Pair(previous, splitScoreElement))
                                }
                                previous = splitScoreElement
                            }

                            // Start new bar
                            currentBar = Bar(score)
                            score.bars.add(currentBar)

                            for (duration in durationsInNextBar) {
                                val splitScoreElement =
                                    NoteOrRest(score.getAndIncrementIdCounter(), duration, isNote, octave, note)

                                if (previous != null) {
                                    score.ties.add(Pair(previous, splitScoreElement))
                                }

                                previous = splitScoreElement
                            }
                        }
                        else -> {
                            remainingTicksInBar -= ticksNeededForElement
                            val scoreHandlerElement = NoteOrRest(
                                element.id,
                                element.duration,
                                isNote,
                                octave,
                                note
                            )
                            currentBar.scoreHandlerElements.add(scoreHandlerElement)
                        }
                    }
                }
                is NoteSequenceElement.MultipleNotesElement -> {
                    // TODO For now assuming that all notes in the group have the same duration
                    val duration = element.elements.first().duration
                    val ticksNeededForElement = duration.ticks

                    if (remainingTicksInBar == 0) {
                        // No more room in bar, start on a new one
                        remainingTicksInBar = ticksPerBar
                        currentBar = Bar(score)
                        score.bars.add(currentBar)
                    }

                    when {
                        // TODO Handle situation when element crosses bar lines

//                        remainingTicksInBar < ticksNeededForElement -> {
//                            val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar)
//                            val durationsInNextBar =
//                                ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar)
//
//                            val previous =
//                                addAndTie(element, durationsInCurrentBar, currentBar, scoreSetup = scoreSetup)
//                            remainingTicksInBar += ticksPerBar - ticksNeededForElement
//                            currentBar = BarData()
//                            bars.add(currentBar)
//                            addAndTie(element, durationsInNextBar, currentBar, previous, scoreSetup = scoreSetup)
//                        }
                        else -> {
                            remainingTicksInBar -= ticksNeededForElement
                            val noteGroupElement = NoteGroup(
                                score.getAndIncrementIdCounter(),
                                element.elements.map {
                                    NoteSymbol(
                                        element.id,
                                        it.duration,
                                        it.octave,
                                        it.note
                                    )
                                })
                            currentBar.scoreHandlerElements.add(noteGroupElement)
                        }
                    }
                }
            }
        }

        logger.debug { "Number of bars: ${score.bars.size}. Remaining ticks in bar: $remainingTicksInBar" }

        var lastBarTrimmed = false
        if (trimEndBars && score.bars.size > 1) {
            val barsBeforeTrimming = score.bars.size
            trimBars(score.bars)
            lastBarTrimmed = barsBeforeTrimming != score.bars.size
        }

        logger.debug { "After trimming. Number of bars: ${score.bars.size}. Remaining ticks in bar: $remainingTicksInBar" }

        if (!lastBarTrimmed) {
            fillInLastBar(score.bars, remainingTicksInBar, score)
        }

        logger.debug { "After fill in. Number of bars: ${score.bars.size}. Remaining ticks in bar: $remainingTicksInBar" }

        return score
    }

    private fun fillInLastBar(bars: MutableList<Bar>, ticksRemainingInBar: Int, score: Score) {
        if (bars.isEmpty() || ticksRemainingInBar == 0) {
            return
        }

        bars.last().let { lastBar ->
            ScoreHandlerUtilities.splitIntoDurations(ticksRemainingInBar).forEach {
                val scoreHandlerElement =
                    NoteOrRest((score.getAndIncrementIdCounter()), it, false, 5, NoteType.C)
                lastBar.scoreHandlerElements.add(scoreHandlerElement)
            }
        }
    }

    private fun trimBars(bars: MutableList<Bar>) {
        if (bars.isEmpty()) {
            return
        }
        bars.takeLastWhile { bar -> bar.scoreHandlerElements.all { it is NoteOrRest && !it.isNote } }
            .forEach { bars.remove(it) }

    }


}