import com.github.aakira.napier.Napier
import com.kjipo.handler.ScoreHandlerListener
import com.kjipo.handler.ScoreHandlerWrapper
import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.ActionScript
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.SequenceGenerator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

class WebscoreShow(private val midiInterface: MidiPlayerInterface) {

    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
    private var targetSequenceGenerator = SequenceGenerator()
    private var inputSequenceGenerator = SequenceGenerator()
    private var webScore: WebScore? = null
    private var inputScore: WebScore? = null

    fun createSequence() {
        val tempNoteSequence = polyphonicNoteSequenceGenerator.createSequence()

        targetSequenceGenerator = SequenceGenerator()
        targetSequenceGenerator.loadSimpleNoteSequence(tempNoteSequence)

        webScore = WebScore(ScoreHandlerJavaScript(targetSequenceGenerator), "targetScore", false)
    }

    fun createInputScore() {
        inputSequenceGenerator = SequenceGenerator()
        val scoreHandlerWrapper = ScoreHandlerWrapper(inputSequenceGenerator)

        scoreHandlerWrapper.addListener(object : ScoreHandlerListener {
            override fun pitchSequenceChanged() {
               console.log("Test24")
            }
        })

        inputScore = WebScore(ScoreHandlerJavaScript(scoreHandlerWrapper), "inputScore")
    }

    suspend fun playSequence() {
        playSequenceInternal(targetSequenceGenerator.getActionSequenceScript())
    }

    suspend fun playInputSequence() {
        val actionScript = inputSequenceGenerator.getActionSequenceScript()

        console.log("Test23: ${actionScript.timeEventList.size}")

        playSequenceInternal(actionScript)
    }

    private suspend fun playSequenceInternal(actionScript: ActionScript) {
        val activePitches = mutableSetOf<Int>()
        actionScript.timeEventList.forEach {
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
                Napier.d("Off-messages sent", tag = "Midi")
                throw e
            }

        }
    }
}

