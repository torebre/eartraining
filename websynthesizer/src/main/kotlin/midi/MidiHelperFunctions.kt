package com.kjipo.midi

import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.ActionScript
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}


suspend fun playTargetSequenceInternal2(
    actionScript: ActionScript,
    midiInterface: MidiPlayerInterface,
    highlightCallback: ((ids: Collection<String>, hightlightOn: Boolean) -> Unit)? = null
) {
    val activePitches = mutableSetOf<Int>()
    actionScript.timeEventList.forEach {
        val sleepTime = it.first
        val events = it.second

        try {
            delay(sleepTime.toLong())

            events.forEach { action ->
                when (action) {
                    is Action.PitchEvent -> {
                        if (action.noteOn) {

//                            logger.info { "Pitch on: ${action.pitches}. Time: ${Date.now()}" }

                            action.pitches.forEach { pitch ->
                                activePitches.add(pitch)
                                midiInterface.noteOn(pitch)
                            }
                        } else {
                            action.pitches.forEach { pitch ->
                                activePitches.remove(pitch)
                                midiInterface.noteOff(pitch)
                            }
                        }
                    }

                    is Action.HighlightEvent -> {
                        highlightCallback?.let { it(action.ids, action.highlightOn) }
//                        webScore?.apply {
//                            if (action.highlightOn) {
//                                highlight(action.ids)
//                            } else {
//                                removeHighlight(action.ids)
//                            }
//                        }
                    }
                }

//                for (pitchEvent in it.first) {
//                    if (pitchEvent.on) {
//                        Napier.d("Pitch on: ${pitchEvent.pitch}", tag = "Midi")
//                        midiPlayer.noteOn(pitchEvent.pitch)
//                        Napier.d("On-message sent", tag = "Midi")
//                    } else {
//                        Napier.d("Pitch off: ${pitchEvent.pitch}", tag = "Midi")
//                        midiPlayer.noteOff(pitchEvent.pitch)
//                        Napier.d("Off-message sent", tag = "Midi")
//                    }
//                }

            }
        } catch (e: CancellationException) {
            for (pitch in activePitches) {
                midiInterface.noteOff(pitch)
            }
            throw e
        }

    }
}
