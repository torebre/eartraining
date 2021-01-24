import com.kjipo.handler.ScoreHandler
import com.kjipo.midi.SimplePitchEvent
import com.kjipo.scoregenerator.PolyphonicNoteSequenceGenerator
import com.kjipo.scoregenerator.SequenceGenerator
import com.kjipo.scoregenerator.SimpleNoteSequence

class WebscoreShow {

    private val synthesizer = SynthesizerScript()
    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()

    private var noteSequence: SimpleNoteSequence? = null
    private var midiEventSequence: List<Pair<Collection<SimplePitchEvent>, Int>>? = null
    private var midiScript: PolyphonicSequenceScript? = null


    fun createSequence() {
        val tempNoteSequence = polyphonicNoteSequenceGenerator.createSequence()

        val sequenceGenerator = SequenceGenerator()
        sequenceGenerator.loadSimpleNoteSequence(tempNoteSequence)

        val tempMidiEventSequence =
            PolyphonicNoteSequenceGenerator.transformToSimplePitchEventSequence(tempNoteSequence)
        midiScript = PolyphonicSequenceScript(tempMidiEventSequence, synthesizer)

        noteSequence = tempNoteSequence
        midiEventSequence = tempMidiEventSequence

        WebScore(ScoreHandlerJavaScript(sequenceGenerator))
    }

    suspend fun playSequence() {
        midiScript?.play()
    }


}