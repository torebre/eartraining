import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.ActionScript
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay


internal suspend fun playTargetSequenceInternal(actionScript: ActionScript, webScore: WebScore?, midiInterface: MidiPlayerInterface) {
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
                            action.pitches.forEach {
                                activePitches.add(it)
                                midiInterface.noteOn(it)
                            }
                        } else {
                            action.pitches.forEach {
                                activePitches.remove(it)
                                midiInterface.noteOff(it)
                            }
                        }
                    }
                    is Action.HighlightEvent -> {
                        webScore?.apply {
                            if (action.highlightOn) {
                                highlight(action.ids)
                            } else {
                                removeHighlight(action.ids)
                            }
                        }
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
