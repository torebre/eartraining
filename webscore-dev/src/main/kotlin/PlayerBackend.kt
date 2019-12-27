import com.github.aakira.napier.Napier
import com.kjipo.scoregenerator.Action
import com.kjipo.scoregenerator.SequenceGenerator
import com.kjipo.scoregenerator.SimpleSequenceGenerator
import kotlinx.coroutines.*

class PlayerBackend() {
    var simpleNoteSequence = SimpleSequenceGenerator.createSequence()
    val sequenceGenerator = SequenceGenerator()

    val webScore = WebScore(ScoreHandlerJavaScript(sequenceGenerator))

    val synthesizerScript = SynthesizerScript()

    var midiScript = MidiScript(sequenceGenerator.pitchSequence, synthesizerScript)
    var currentPlayJob: Job? = null

    init {
        sequenceGenerator.loadSimpleNoteSequence(simpleNoteSequence)
    }

    fun playSequence() {
        val previousPlayJob = currentPlayJob

        currentPlayJob = GlobalScope.launch {
            previousPlayJob?.cancelAndJoin()

            console.log("Playing sequence: ${sequenceGenerator.pitchSequence}")

            midiScript = MidiScript(sequenceGenerator.pitchSequence, synthesizerScript)
            midiScript.play()
        }
    }

    fun createSequence() {
        val sequence = SimpleSequenceGenerator.createSequence()

        midiScript = MidiScript(sequence.transformToPitchSequence(), synthesizerScript)
        sequenceGenerator.loadSimpleNoteSequence(sequence)

        webScore.reload()
    }

    suspend fun playCurrent() {
        var currentTime = 0
        for(action in sequenceGenerator.actionSequence) {
            if(action.time > currentTime) {
                val sleepTime = action.time - currentTime
                currentTime += sleepTime

                try {
                    delay(sleepTime.toLong())
                } catch (e: CancellationException) {
                    synthesizerScript.releaseAll()
                    throw e
                }
            }

            when(action) {
                is Action.PitchEvent -> {
                    if(action.noteOn) {
                        synthesizerScript.noteOn(action.pitch)
                    }
                    else {
                        synthesizerScript.noteOff(action.pitch)
                    }
                }
                is Action.HighlightEvent -> {
                    if(action.highlightOn) {
                        for (id in action.ids) {
                            webScore.highlight(id)
                        }
                    }
                    else {
                        for(id in action.ids) {
                            webScore.removeHighlight(id)
                        }
                    }
                }
            }
        }


    }





}