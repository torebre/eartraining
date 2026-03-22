import com.kjipo.attemptprocessor.PitchData
import com.kjipo.midi.playTargetSequenceInternal2
import com.kjipo.scoregenerator.*
import graph.PitchDataWithTime
import graph.PitchGraph
import graph.PitchGraphModel
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ui.AppState
import kotlin.math.pow

class AppController(val state: AppState) {
    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
    private val synthesizer = com.kjipo.midi.SynthesizerScript()
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
