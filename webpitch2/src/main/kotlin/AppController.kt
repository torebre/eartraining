import com.kjipo.attemptprocessor.PitchData
import com.kjipo.midi.MidiPlayerInterface
import com.kjipo.midi.playTargetSequenceInternal2
import com.kjipo.scoregenerator.*
import graph.PitchDataWithTime
import graph.PitchGraph
import graph.PitchGraphModel
import graph.getMidiNoteClosestToFrequency
import graph.midiNoteToFrequency
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import storage.APPLICATION_VERSION
import storage.AttemptStorage
import storage.ExportedAttempts
import storage.LocalStorageAttemptStorage
import storage.downloadJsonFile
import ui.AppState
import ui.ConfigMode

class AppController(
    val state: AppState,
    val attemptStorage: AttemptStorage = LocalStorageAttemptStorage(),
//    private val synthesizer = com.kjipo.midi.SynthesizerScript()
    private val synthesizer: MidiPlayerInterface = com.kjipo.soundfontsyntheizer.SoundfontSynthesizer()
) {
    private val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
    val rateInput = RateInput()
    private val pitchGraphModel = PitchGraphModel()
    private val pitchDetection = PitchDetection()
    private val logger = KotlinLogging.logger {}

    private var actionSequence: List<Action> = emptyList()
    private var firstNote: Int? = null
    var pitchGraph: PitchGraph? = null

    init {
        pitchDetection.addPitchDetectionListener(object : PitchDetectionListener {
            override fun pitchData(pitchData: PitchData) {
                state.currentPitch = pitchData.pitch
                state.currentCertainty = pitchData.certainty
                if (state.isRecording) {
                    rateInput.addPitchData(pitchData)
                }

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
        if (state.isPlayingFirstNote) {
            toggleFirstNote()
        }
        val pitchRange = PitchRange(state.lowestNote, state.highestNote)
        val currentSequence = polyphonicNoteSequenceGenerator.createSequence(false, pitchRange)
        val (pitches, actions) = computePitchSequence(currentSequence.elements)
        actionSequence = actions
        firstNote = pitches.minByOrNull { it.timeOn }?.pitch
        rateInput.setCurrentTarget(pitches)
        rateInput.startNewInput()
        state.reset()
        pitchGraphModel.reset(simpleNoteSequenceToPitchSequence(currentSequence))
    }

    fun play() {
        GlobalScope.launch(Dispatchers.Default) {
            playTargetSequenceInternal2(actionScript(actionSequence), synthesizer)
        }
    }

    fun toggleFirstNote() {
        if (state.isRecording) {
            return
        }
        if (!state.isPlayingFirstNote) {
            val note = firstNote ?: return
            // Only the first note should be shown, not the whole target sequence
            if (pitchGraphModel.isTargetSequenceShowing()) {
                pitchGraphModel.toggleTargetSequenceShowing()
            }
            pitchGraphModel.clearPitchData()
            pitchGraphModel.setReferencePitch(midiNoteToFrequency(note))
            synthesizer.noteOn(note)
            startPitchDetection()
            state.isPlayingFirstNote = true
        } else {
            firstNote?.let { synthesizer.noteOff(it) }
            stopPitchDetection()
            state.isPlayingFirstNote = false
        }
    }

    fun toggleRecording() {
        if (state.isPlayingFirstNote) {
            return
        }
        if (!state.isRecording) {
            state.canSaveAttempt = false
            rateInput.startNewInput()
            pitchGraphModel.clearPitchData()
            startPitchDetection()
            state.isRecording = true
        } else {
            stopPitchDetection()
            rateInput.stopInput()
            state.isRecording = false
            if (state.configMode == ConfigMode.NORMAL) {
                state.canSaveAttempt = true
            }
        }
    }

    private fun startPitchDetection() {
        pitchDetection.startRecording()
        pitchDetection.addPitchDetectionListener(pitchGraphModel)
    }

    private fun stopPitchDetection() {
        pitchDetection.stopRecording()
        pitchDetection.removePitchDetectionListener(pitchGraphModel)
    }

    fun saveAttempt() {
        if (state.canSaveAttempt) {
            rateInput.save(attemptStorage)
            state.canSaveAttempt = false
        }
    }

    fun exportAttempts() {
        val attempts = attemptStorage.getAttempts()
        val attemptsArray = attempts.map { LocalStorageAttemptStorage.attemptToJson(it) }.toTypedArray()
        val exportObj = js("{}")
        exportObj["version"] = APPLICATION_VERSION
        exportObj["attempts"] = attemptsArray
        val jsonString = JSON.stringify(exportObj, replacer = null, space = 2)
        downloadJsonFile("attempts.json", jsonString)
    }

    fun toggleShowTarget() {
        if (state.isPlayingFirstNote) {
            return
        }
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

    fun darkModeChanged(isDarkMode: Boolean) {
        pitchGraphModel.darkModeChanged(isDarkMode)
    }

    private fun simpleNoteSequenceToPitchSequence(simpleNoteSequence: SimpleNoteSequence): List<PitchDataWithTime> {
        var idCounter = 0
        return simpleNoteSequence.transformToPitchSequence().flatMap { pitchData ->
            val pitch = midiNoteToFrequency(pitchData.pitch)
            listOf(
                PitchDataWithTime(pitch, 1.0f, pitchData.timeOn.toLong(), idCounter++),
                PitchDataWithTime(pitch, 1.0f, pitchData.timeOff.toLong(), idCounter++)
            )
        }
    }
}
