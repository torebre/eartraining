import com.github.aakira.napier.Napier
import com.kjipo.midi.SimplePitchEvent
import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.SequenceGenerator
import com.kjipo.scoregenerator.SimpleNoteSequence
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class WebscoreShow(private val midiInterface: MidiPlayerInterface) {

    //    private val synthesizer = SynthesizerScript()
    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()

//    private var noteSequence: SimpleNoteSequence? = null
//    private var midiEventSequence: List<Pair<Collection<SimplePitchEvent>, Int>>? = null
//    private var midiScript: PolyphonicSequenceScript? = null

    private var sequenceGenerator = SequenceGenerator()
    private var webScore: WebScore? = null

    fun createSequence() {
        val tempNoteSequence = polyphonicNoteSequenceGenerator.createSequence()

        sequenceGenerator = SequenceGenerator()
        sequenceGenerator.loadSimpleNoteSequence(tempNoteSequence)

//        val tempMidiEventSequence =
//            PolyphonicNoteSequenceGenerator.transformToSimplePitchEventSequence(tempNoteSequence)
//        midiScript = PolyphonicSequenceScript(tempMidiEventSequence, synthesizer)


//        noteSequence = tempNoteSequence
//        midiEventSequence = tempMidiEventSequence

        println("Test30")
        Napier.i("Test31")
        console.log("Test32")

        WebScore(ScoreHandlerJavaScript(sequenceGenerator))
    }

    suspend fun playSequence() {

        console.log("Test23: ${sequenceGenerator.getActionSequenceScript().timeEventList.size}")

        sequenceGenerator.getActionSequenceScript().timeEventList.forEach {
            val sleepTime = it.first
            val events = it.second
            var activePitches = mutableSetOf<Int>()

            console.log("Test24: $it")

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
                            if (action.highlightOn) {
                                webScore?.highlight(action.ids)
                            } else {
                                webScore?.removeHighlight(action.ids)
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
//        midiScript?.play()
}

