import com.github.aakira.napier.Napier
import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.SequenceGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class WebscoreShow(private val midiInterface: MidiPlayerInterface) {

    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
    private var sequenceGenerator = SequenceGenerator()
    private var webScore: WebScore? = null

    fun createSequence() {
        val tempNoteSequence = polyphonicNoteSequenceGenerator.createSequence()

        sequenceGenerator = SequenceGenerator()
        sequenceGenerator.loadSimpleNoteSequence(tempNoteSequence)

        webScore = WebScore(ScoreHandlerJavaScript(sequenceGenerator))
    }

    suspend fun playSequence() {
        val activePitches = mutableSetOf<Int>()
        sequenceGenerator.getActionSequenceScript().timeEventList.forEach {
            val sleepTime = it.first
            val events = it.second

            console.log("Sleep time: ${sleepTime.toLong()}")

            try {
                delay(sleepTime.toLong())

                events.forEach { action ->
                    when (action) {
                        is Action.PitchEvent -> {

                            console.log("Notes: ${action.pitches}. Note on: ${action.noteOn}")

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
                            webScore?.let {
                                if (action.highlightOn) {
                                    it.highlight(action.ids)
                                } else {
                                    it.removeHighlight(action.ids)
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
                Napier.d("Off-messages sent", tag = "Midi")
                throw e
            }

        }

    }
}

