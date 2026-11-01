import com.kjipo.attemptprocessor.InputAttempt
import com.kjipo.attemptprocessor.PitchData
import com.kjipo.scoregenerator.Pitch
import io.github.oshai.kotlinlogging.KotlinLogging
import storage.AttemptStorage


class RateInput {

    private val currentTarget: MutableList<Pitch> = mutableListOf()
    private val currentInput: MutableList<PitchData> = mutableListOf()

    private val logger = KotlinLogging.logger {}

    fun addPitchData(pitchData: PitchData) {
        currentInput.add(pitchData)
    }

    fun startNewInput() {
        currentInput.clear()
    }

    fun stopInput() {
        logger.info { "Stopped input with ${currentInput.size} pitch data points" }
    }

    fun setCurrentTarget(pitches: List<Pitch>) {
        currentTarget.clear()
        currentTarget.addAll(pitches)
    }

    fun getCurrentAttempt(): InputAttempt {
        return InputAttempt(currentTarget.toList(), currentInput.toList())
    }

    fun save(attemptStorage: AttemptStorage) {
        val inputAttempt = getCurrentAttempt()
        attemptStorage.saveAttempt(inputAttempt)
        logger.info { "Saved input attempt to storage" }
    }

}