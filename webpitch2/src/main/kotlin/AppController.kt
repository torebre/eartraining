import com.kjipo.attemptprocessor.PitchData
import com.kjipo.midi.MidiPlayerInterface
import com.kjipo.midi.playTargetSequenceInternal2
import com.kjipo.scoregenerator.*
import graph.PitchDataWithTime
import graph.PitchGraph
import graph.PitchGraphModel
import graph.getMidiNoteClosestToFrequency
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ui.AppState
import ui.ConfigMode
import kotlin.math.pow

class AppController(val state: AppState) {
    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
//    private val synthesizer = com.kjipo.midi.SynthesizerScript()
    private val synthesizer: MidiPlayerInterface = com.kjipo.soundfontsyntheizer.SoundfontSynthesizer()
    private val rateInput = RateInput()
    private val pitchGraphModel = PitchGraphModel()
    private val pitchDetection = PitchDetection()
    private val logger = KotlinLogging.logger {}

    private var actionSequence: List<Action> = emptyList()
    var pitchGraph: PitchGraph? = null

    init {
        pitchDetection.addPitchDetectionListener(object : PitchDetectionListener {
            override fun pitchData(pitchData: PitchData) {
                state.currentPitch = pitchData.pitch
                state.currentCertainty = pitchData.certainty
                rateInput.addPitchData(pitchData)

                if (state.configMode != ConfigMode.NORMAL) {
                    if (pitchData.pitch > 0) {
                        state.closestMidiNote = getMidiNoteClosestToFrequency(pitchData.pitch)
                    } else {
                        state.closestMidiNote = null
                    }
                }
            }
        })
    }

    fun setPitchGraphElement(svgElementId: String) {
        pitchGraph = PitchGraph(svgElementId, pitchGraphModel)
    }

    fun generateSequence() {
        val pitchRange = PitchRange(state.lowestNote, state.highestNote)
        val currentSequence = polyphonicNoteSequenceGenerator.createSequence(false, pitchRange)
        val (pitches, actions) = computePitchSequence(currentSequence.elements)
        actionSequence = actions
        rateInput.setCurrentTarget(pitches)
        rateInput.startNewInput()
        pitchGraphModel.reset(simpleNoteSequenceToPitchSequence(currentSequence))
    }

    fun play() {
        GlobalScope.launch(Dispatchers.Default) {
            playTargetSequenceInternal2(actionScript(actionSequence), synthesizer)
        }
    }

    fun toggleRecording() {
        if (!state.isRecording) {
            rateInput.startNewInput()
            pitchDetection.startRecording()
            pitchDetection.addPitchDetectionListener(pitchGraphModel)
            state.isRecording = true
        } else {
            pitchDetection.stopRecording()
            rateInput.stopInput()
            state.isRecording = false
        }
    }

    fun toggleShowTarget() {
        pitchGraphModel.toggleTargetSequenceShowing()
    }

    fun startConfigureLowerLimit() {
        state.configMode = ConfigMode.CONFIGURING_LOWER
        if (!state.isRecording) {
            toggleRecording()
        }
    }

    fun startConfigureUpperLimit() {
        state.configMode = ConfigMode.CONFIGURING_UPPER
        if (!state.isRecording) {
            toggleRecording()
        }
    }

    fun setLimit() {
        state.closestMidiNote?.let {
            if (state.configMode == ConfigMode.CONFIGURING_LOWER) {
                state.lowestNote = it
            } else if (state.configMode == ConfigMode.CONFIGURING_UPPER) {
                state.highestNote = it
            }
        }
        state.configMode = ConfigMode.NORMAL
        state.closestMidiNote = null
        if (state.isRecording) {
            toggleRecording()
        }
    }

    private fun simpleNoteSequenceToPitchSequence(simpleNoteSequence: SimpleNoteSequence): List<PitchDataWithTime> {
        var idCounter = 0
        return simpleNoteSequence.transformToPitchSequence().flatMap { pitchData ->
            val pitch = (440 * 2.0.pow((pitchData.pitch - 69) / 12.0)).toFloat()
            listOf(
                PitchDataWithTime(pitch, 1.0f, pitchData.timeOn.toLong(), idCounter++),
                PitchDataWithTime(pitch, 1.0f, pitchData.timeOff.toLong(), idCounter++)
            )
        }
    }
}
