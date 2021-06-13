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
        var currentBar = Bar()
        currentBar.clef = Clef.G
        currentBar.timeSignature = TimeSignature(4, 4)
        score.bars.add(currentBar)

        for (element in noteSequenceElements) {
            when (element) {
                is NoteSequenceElement.NoteElement -> {
                    val pair = handleNoteElement(element, remainingTicksInBar, currentBar, score)
                    currentBar = pair.first
                    remainingTicksInBar = pair.second
                }
                is NoteSequenceElement.RestElement -> {
                    val pair = handleRestElement(element, remainingTicksInBar, currentBar, score)
                    currentBar = pair.first
                    remainingTicksInBar = pair.second
                }
                is NoteSequenceElement.MultipleNotesElement -> {
                    val pair = handleMultipleNotesElement(element, remainingTicksInBar, currentBar, score)
                    currentBar = pair.first
                    remainingTicksInBar = pair.second
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

    private fun handleNoteElement(
        noteElement: NoteSequenceElement.NoteElement,
        remainingTicksInBar: Int,
        currentBar: Bar,
        score: Score
    ): Pair<Bar, Int> {
        var remainingTicksInBar1 = remainingTicksInBar
        var currentBar1 = currentBar
        val ticksNeededForElement = noteElement.duration.ticks

        if (remainingTicksInBar1 == 0) {
            // No more room in bar, start on a new one
            remainingTicksInBar1 = ticksPerBar
            currentBar1 = Bar()
            score.bars.add(currentBar1)
        }

        when {
            remainingTicksInBar1 < ticksNeededForElement -> {
                val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar1)
                val durationsInNextBar =
                    ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar1)
                var previous: ScoreHandlerElement? = null

                for (duration in durationsInCurrentBar) {
                    val splitScoreElement =
                        Note(
                            noteElement.id,
                            duration,
                            noteElement.octave,
                            noteElement.note,
                            noteElement.properties,
                            requiresStem(noteElement),
                            if (noteRequiresSharp(noteElement.note)) {
                                Accidental.SHARP
                            } else {
                                null
                            }
                        )
                    currentBar.scoreHandlerElements.add(splitScoreElement)

                    if (previous != null) {
                        score.ties.add(Pair(previous, splitScoreElement))
                    }
                    previous = splitScoreElement
                }

                // Start new bar
                currentBar1 = Bar()
                score.bars.add(currentBar1)

                for (duration in durationsInNextBar) {
                    val splitScoreElement =
                        Note(
                            score.getAndIncrementIdCounter(),
                            duration,
                            noteElement.octave,
                            noteElement.note,
                            noteElement.properties,
                            requiresStem(noteElement),
                            if (noteRequiresSharp(noteElement.note)) {
                                Accidental.SHARP
                            } else {
                                null
                            }
                        )

                    if (previous != null) {
                        score.ties.add(Pair(previous, splitScoreElement))
                    }
                    currentBar1.scoreHandlerElements.add(splitScoreElement)
                    previous = splitScoreElement
                }
            }
            else -> {
                remainingTicksInBar1 -= ticksNeededForElement
                val scoreHandlerElement = Note(
                    noteElement.id,
                    noteElement.duration,
                    noteElement.octave,
                    noteElement.note,
                    noteElement.properties,
                    requiresStem(noteElement),
                    if (noteRequiresSharp(noteElement.note)) {
                        Accidental.SHARP
                    } else {
                        null
                    }
                )
                currentBar1.scoreHandlerElements.add(scoreHandlerElement)
            }
        }
        return Pair(currentBar1, remainingTicksInBar1)
    }


    private fun handleRestElement(
        element: NoteSequenceElement.RestElement,
        remainingTicksInBar: Int,
        currentBar: Bar,
        score: Score
    ): Pair<Bar, Int> {
        var remainingTicksInBar1 = remainingTicksInBar
        var currentBar1 = currentBar
        val ticksNeededForElement = element.duration.ticks

        if (remainingTicksInBar1 == 0) {
            // No more room in bar, start on a new one
            remainingTicksInBar1 = ticksPerBar
            currentBar1 = Bar()
            score.bars.add(currentBar1)
        }

        when {
            remainingTicksInBar1 < ticksNeededForElement -> {
                val durationsInCurrentBar = ScoreHandlerUtilities.splitIntoDurations(remainingTicksInBar1)
                val durationsInNextBar =
                    ScoreHandlerUtilities.splitIntoDurations(ticksNeededForElement - remainingTicksInBar1)

                var previous: ScoreHandlerElement? = null
                for (duration in durationsInCurrentBar) {
                    val splitScoreElement =
                        Rest(element.id, duration, element.properties)
                    currentBar1.scoreHandlerElements.add(splitScoreElement)

                    if (previous != null) {
                        score.ties.add(Pair(previous, splitScoreElement))
                    }
                    previous = splitScoreElement
                }

                // Start new bar
                currentBar1 = Bar()
                score.bars.add(currentBar1)

                for (duration in durationsInNextBar) {
                    val splitScoreElement =
                        Rest(
                            score.getAndIncrementIdCounter(),
                            duration,
                            element.properties
                        )
                    currentBar1.scoreHandlerElements.add(splitScoreElement)

                    if (previous != null) {
                        score.ties.add(Pair(previous, splitScoreElement))
                    }

                    previous = splitScoreElement
                }
            }
            else -> {
                remainingTicksInBar1 -= ticksNeededForElement
                val scoreHandlerElement = Rest(
                    element.id,
                    element.duration,
                    element.properties
                )
                currentBar1.scoreHandlerElements.add(scoreHandlerElement)
            }
        }
        return Pair(currentBar1, remainingTicksInBar1)
    }

    private fun handleMultipleNotesElement(
        element: NoteSequenceElement.MultipleNotesElement,
        remainingTicksInBar: Int,
        currentBar: Bar,
        score: Score
    ): Pair<Bar, Int> {
        // TODO For now assuming that all notes in the group have the same duration
        var remainingTicksInBar1 = remainingTicksInBar
        var currentBar1 = currentBar
        val duration = element.elements.first().duration
        val ticksNeededForElement = duration.ticks

        if (remainingTicksInBar1 == 0) {
            // No more room in bar, start on a new one
            remainingTicksInBar1 = ticksPerBar
            currentBar1 = Bar()
            score.bars.add(currentBar1)
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
                remainingTicksInBar1 -= ticksNeededForElement
                val noteGroupElement = NoteGroup(
                    score.getAndIncrementIdCounter(),
                    element.elements.map {
                        NoteSymbol(
                            it.id,
                            it.duration,
                            it.octave,
                            it.note
                        )
                    }, element.properties,
                    requiresStem(element)
                )
                currentBar1.scoreHandlerElements.add(noteGroupElement)
            }
        }
        return Pair(currentBar1, remainingTicksInBar1)
    }

    private fun fillInLastBar(bars: MutableList<Bar>, ticksRemainingInBar: Int, score: Score) {
        if (bars.isEmpty() || ticksRemainingInBar == 0) {
            return
        }

        bars.last().let { lastBar ->
            ScoreHandlerUtilities.splitIntoDurations(ticksRemainingInBar).forEach {
                val scoreHandlerElement =
                    Rest(score.getAndIncrementIdCounter(), it, emptyMap())
                lastBar.scoreHandlerElements.add(scoreHandlerElement)
            }
        }
    }

    private fun trimBars(bars: MutableList<Bar>) {
        if (bars.isEmpty()) {
            return
        }
        bars.takeLastWhile { bar -> bar.scoreHandlerElements.all { it is Rest } }
            .forEach { bars.remove(it) }
    }


    // TODO This only works for C scale and G clef
    private fun stemUp(pitch: Int) = if (pitch >= 71) {
        Stem.DOWN
    } else {
        Stem.UP
    }

    private fun requiresStem(noteElement: NoteSequenceElement.NoteElement): Stem {
        return noteElement.duration.let {
            // TODO Make proper computation
            if (it == Duration.HALF || it == Duration.QUARTER || it == Duration.EIGHT) {
                stemUp(ScoreHandlerUtilities.getPitch(noteElement.note, noteElement.octave))
            } else {
                Stem.NONE
            }
        }
    }


    private fun requiresStem(multipleNotesElements: NoteSequenceElement.MultipleNotesElement): Stem {
        return multipleNotesElements.duration.let {
            // TODO Make proper computation
            if (it == Duration.HALF || it == Duration.QUARTER || it == Duration.EIGHT) {
                // TODO Make proper computation to see whether the stem should be up or down
                multipleNotesElements.elements.first().let { noteElement ->
                    stemUp(ScoreHandlerUtilities.getPitch(noteElement.note, noteElement.octave))
                }
            } else {
                Stem.NONE
            }
        }
    }
}