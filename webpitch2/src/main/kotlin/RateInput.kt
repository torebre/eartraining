import com.kjipo.attemptprocessor.InputAttempt
import com.kjipo.attemptprocessor.PitchData
import com.kjipo.scoregenerator.Pitch
import io.github.oshai.kotlinlogging.KotlinLogging


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
        // TODO Need to do something here?

        save()
    }

    fun setCurrentTarget(pitches: MutableList<Pitch>) {
        currentTarget.addAll(pitches)
    }

    fun save() {
        val inputAttempt = InputAttempt(currentTarget.toList(), currentInput.toList())
        val outputJson = JSON.stringify(inputAttempt)

        logger.info { "Input data: $outputJson" }

        // TODO Save input attempt


    }


}