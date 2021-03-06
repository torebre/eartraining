import com.kjipo.handler.InsertNoteWithType
import com.kjipo.score.Accidental
import com.kjipo.score.Duration
import com.kjipo.score.GClefNoteLine
import com.kjipo.score.NoteType
import mu.KotlinLogging
import org.w3c.dom.events.KeyboardEvent
import kotlin.math.log


class NoteInput(val scoreHandler: ScoreHandlerJavaScript) {

    private enum class NoteInputStep {
        Note,
        Modifier,
        Octave
    }

    private enum class Modifier {
        Accidental
    }

    private data class NoteInput(
        val duration: Duration,
        var note: GClefNoteLine? = null,
        var modifier: Modifier? = null,
        var octave: Int? = null
    )

    private var currentStep: NoteInputStep? = null
    private var currentNoteInput: NoteInput? = null

    private val logger = KotlinLogging.logger {}


    private fun clear() {
        currentStep = null
        currentNoteInput = null
    }

    fun processInput(keyboardEvent: KeyboardEvent): Boolean {
        if (keyboardEvent.key == "Escape") {
            clear()
            return true
        }

        var insertedNote = false
        when (currentStep) {
            null -> {
                if (keyboardEvent.key in setOf("1", "2", "3", "4", "5")) {
                    getDuration(keyboardEvent.keyCode - 48).let {
                        currentNoteInput = NoteInput(it)
                        currentStep = NoteInputStep.Note
                    }
                }
            }
            NoteInputStep.Note -> {
                // TODO Handle other notes
                when (keyboardEvent.key) {
                    "c" -> handleNoteStepInput(GClefNoteLine.C)
                    "d" -> handleNoteStepInput(GClefNoteLine.D)
                    "e" -> handleNoteStepInput(GClefNoteLine.E)
                    "f" -> handleNoteStepInput(GClefNoteLine.F)
                    "g" -> handleNoteStepInput(GClefNoteLine.G)
                    "a" -> handleNoteStepInput(GClefNoteLine.A)
                    "h" -> handleNoteStepInput(GClefNoteLine.H)
                }
            }
            NoteInputStep.Modifier -> {
                val keyToDigit = keyboardEvent.key.toIntOrNull()

                if (keyToDigit == null) {
                    when (keyboardEvent.key) {
                        "#" -> handleModifierStepInput(Accidental.SHARP)
                    }
                    currentStep = NoteInputStep.Octave
                } else {
                    jumpToOctaveInput(keyToDigit)
                    insertedNote = true
                }
            }
            NoteInputStep.Octave -> {
                keyboardEvent.key.toIntOrNull()?.let { octave ->
                    jumpToOctaveInput(octave)
                    insertedNote = true
                }
            }
        }
        return insertedNote
    }

    private fun jumpToOctaveInput(octave: Int) {
        if (octave in 1..12) {
            currentNoteInput?.octave = octave
            insertCurrentNote()
        }
    }

    private fun insertCurrentNote() {
        currentNoteInput?.let {
            it.octave?.let { octave ->
                it.note?.let { note ->
                    // TODO This should be done in a more clear way
                    val noteType = when (it.modifier) {
                        null -> {
                            when (it.note) {
                                GClefNoteLine.A -> NoteType.A
                                GClefNoteLine.C -> NoteType.C
                                GClefNoteLine.D -> NoteType.D
                                GClefNoteLine.F -> NoteType.F
                                GClefNoteLine.G -> NoteType.G
                                GClefNoteLine.H -> NoteType.H
                                GClefNoteLine.E -> NoteType.E
                                else -> {
                                    throw IllegalStateException("Unknown note type: ${it.note}")
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
                    scoreHandler.applyOperation(InsertNoteWithType(null, noteType, octave, it.duration))
                }
            }
        }
    }

    private fun handleModifierStepInput(inputModifier: Accidental) {
        currentNoteInput?.run {
            // TODO Handle other cases
            when (inputModifier) {
                Accidental.SHARP -> modifier = Modifier.Accidental
            }
        }
    }

    private fun handleNoteStepInput(inputNoteType: GClefNoteLine) {
        currentNoteInput?.note = inputNoteType
        currentStep = NoteInputStep.Modifier
    }

}