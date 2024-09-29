package com.kjipo.midi

import com.kjipo.midi.SimplePitchEvent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import mu.KotlinLogging

class PolyphonicSequenceScript(
    private val pitchEvents: List<Pair<Collection<SimplePitchEvent>, Int>>,
    private val midiPlayer: MidiPlayerInterface
) {

    private val logger = KotlinLogging.logger {}

    suspend fun play() {
        console.log("All pitch events: ${pitchEvents}") //, tag = "Midi")

        pitchEvents.forEach {
            val sleepTime = it.second

            console.log("Current pitch events: ${it.first}") //, tag = "Midi")
            logger.debug { "Sleeping for $sleepTime milliseconds" }

            try {
                delay(sleepTime.toLong())

                for (pitchEvent in it.first) {
                    if (pitchEvent.on) {
                        logger.debug { "Pitch on: ${pitchEvent.pitch}" }
                        midiPlayer.noteOn(pitchEvent.pitch)
                        logger.debug { "On-message sent" }
                    } else {
                        logger.debug { "Pitch off: ${pitchEvent.pitch}" }
                        midiPlayer.noteOff(pitchEvent.pitch)
                        logger.debug { "Off-message sent" }
                    }
                }
            } catch (e: CancellationException) {
                for (pitchEvent in it.first) {
                    midiPlayer.noteOff(pitchEvent.pitch)
                }
                logger.debug { "Off-messages sent" }
                throw e
            }
        }
    }
}