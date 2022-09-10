import com.kjipo.handler.InsertNoteWithType
import com.kjipo.score.Duration
import com.kjipo.score.GClefNoteLine
import com.kjipo.score.NoteType
import mu.KotlinLogging
import org.w3c.dom.events.KeyboardEvent


class NoteInput(private val scoreHandler: ScoreHandlerJavaScript) {

    enum class NoteInputStep {
        Duration,
        Note,
        Modifier,
        Octave
    }

    private enum class Modifier {
        None,
        Accidental
    }

    private data class NoteInput(
        val duration: Duration,
        var note: GClefNoteLine? = null,
        var modifier: Modifier? = null,
        var octave: Int? = null
    )

    private var currentStep: NoteInputStep = NoteInputStep.Duration
    private var currentNoteInput: NoteInput? = null
    private var insertNoteNotRest = true

    private val noteInputListeners = mutableListOf<NoteInputListener>()


    private val logger = KotlinLogging.logger {}


    fun clear() {
        currentStep = NoteInputStep.Duration
        currentNoteInput = null
        noteInputListeners.forEach { it.currentStep(currentStep) }
    }

    fun addNoteInputListener(noteInputListener: NoteInputListener) = noteInputListeners.add(noteInputListener)

    fun removeNoteInputListener(noteInputListener: NoteInputListener) = noteInputListeners.remove(noteInputListener)


    fun processInput(keyboardEvent: KeyboardEvent) {
        processInputInternal(keyboardEvent)?.let {
            insertCurrentNote(it)
        }
        noteInputListeners.forEach { it.currentStep(currentStep) }
    }

    private fun processInputInternal(keyboardEvent: KeyboardEvent): NoteInput? {
        if (keyboardEvent.key == "Escape") {
            clear()
        }
        logger.debug { "Current step: $currentStep" }

        when (currentStep) {
            NoteInputStep.Duration -> {
                if (keyboardEvent.key in setOf("1", "2", "3", "4", "5")) {
                    getDuration(keyboardEvent.key.toInt())?.let {
                        currentNoteInput = NoteInput(it)
                        currentStep = NoteInputStep.Note
                    }
                }
            }

            NoteInputStep.Note -> {
                processNoteKeyAndUpdateCurrentStepIfNecessary(keyboardEvent)
            }

            NoteInputStep.Modifier -> {
                val keyToDigit = keyboardEvent.key.toIntOrNull()

                if (keyToDigit == null) {
                    currentNoteInput?.let {
                        it.modifier = when (keyboardEvent.key) {
                            "#" -> Modifier.Accidental
                            else -> Modifier.None
                        }
                    }
                    currentStep = NoteInputStep.Octave
                } else {
                    // Number given as input, interpret that as the octave
                    jumpToOctaveInput(keyToDigit)
                }
            }

            NoteInputStep.Octave -> {
                keyboardEvent.key.toIntOrNull()?.let { octave ->
                    jumpToOctaveInput(octave)
                }
            }
        }

        return currentNoteInput?.let {
            if (noteInputReady(it)) {
                currentNoteInput
            } else {
                null
            }
        }
    }

    private fun processNoteKeyAndUpdateCurrentStepIfNecessary(keyboardEvent: KeyboardEvent) {
        when (keyboardEvent.key) {
            // TODO Handle other notes
            "c" -> handleNoteStepInput(GClefNoteLine.C)
            "d" -> handleNoteStepInput(GClefNoteLine.D)
            "e" -> handleNoteStepInput(GClefNoteLine.E)
            "f" -> handleNoteStepInput(GClefNoteLine.F)
            "g" -> handleNoteStepInput(GClefNoteLine.G)
            "a" -> handleNoteStepInput(GClefNoteLine.A)
            "h" -> handleNoteStepInput(GClefNoteLine.H)
        }
    }


    private fun noteInputReady(noteInput: NoteInput): Boolean {
        return noteInput.note != null && noteInput.octave != null && noteInput.modifier != null
    }

    private fun jumpToOctaveInput(octave: Int) {
        if (octave in 1..12) {
            currentNoteInput?.octave = octave
        }
    }

    private fun insertCurrentNote(noteInput: NoteInput) {
        with(noteInput) {
            octave?.let { octave ->
                note?.let { note ->
                    // TODO This should be done in a more clear way
                    val noteType = when (modifier) {
                        Modifier.None, null -> {
                            when (note) {
                                GClefNoteLine.A -> NoteType.A
                                GClefNoteLine.C -> NoteType.C
                                GClefNoteLine.D -> NoteType.D
                                GClefNoteLine.F -> NoteType.F
                                GClefNoteLine.G -> NoteType.G
                                GClefNoteLine.H -> NoteType.H
                                GClefNoteLine.E -> NoteType.E
                                else -> {
                                    throw IllegalStateException("Unknown note type: $note")
                                }
                            }
                        }

                        Modifier.Accidental -> {
                            when (note) {
                                GClefNoteLine.A -> NoteType.A_SHARP
                                GClefNoteLine.C -> NoteType.C_SHARP
                                GClefNoteLine.D -> NoteType.D_SHARP
                                GClefNoteLine.F -> NoteType.F_SHARP
                                GClefNoteLine.G -> NoteType.G_SHARP
                                GClefNoteLine.H -> NoteType.H
                                GClefNoteLine.E -> NoteType.E
                            }
                        }
                    }
                    clear()
                    scoreHandler.applyOperation(InsertNoteWithType(null, noteType, octave, duration))
                }
            }
        }
    }

    private fun handleNoteStepInput(inputNoteType: GClefNoteLine) {
        currentNoteInput?.note = inputNoteType
        currentStep = NoteInputStep.Modifier
    }

    fun insertNoteNotRest(insertNoteNotRest: Boolean) {
        this.insertNoteNotRest = insertNoteNotRest
    }

}