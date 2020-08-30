import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreHandlerWithState
import com.kjipo.handler.ScoreOperation
import com.kjipo.score.Duration
import kotlinx.serialization.UseSerializers

class ScoreHandlerWithStateImpl(private val scoreHandler: ScoreHandler) : ScoreHandlerWithState {
    private var currentRenderingTree: String? = null
    private var currentDiff: String? = null


    override fun applyOperation(operation: ScoreOperation): String? {
        when (operation) {
            is InsertNote -> {
                handleInsertNote(operation)


            }
            else -> {
                // TODO

            }

            // TODO


        }

        updateCurrentScoreAndGetDiff()
        return currentDiff
    }

    override fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    override fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    override fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    private fun handleInsertNote(insertNote: InsertNote) {
        // Default to quarter if nothing is set
        val duration = insertNote.duration ?: Duration.QUARTER

        if (insertNote.id != null) {
            scoreHandler.insertNote(insertNote.id as String, duration)
        }
        scoreHandler.insertNote(duration)
    }

    private fun updateCurrentScoreAndGetDiff(): String? {
        val scoreAsJson = scoreHandler.getScoreAsJson()
        val tempCurrent = currentRenderingTree

        if (tempCurrent == null) {
            currentRenderingTree = scoreAsJson
            currentDiff = null
        } else {
            currentRenderingTree = scoreAsJson

            val currentContextParsed = JSON.parse<Any>(tempCurrent)
            val updatedDataParsed = JSON.parse<Any>(scoreAsJson)
            val patchOperations = rfc6902.createPatch(currentContextParsed, updatedDataParsed)

            println("Old score: $tempCurrent")
            println("Updated score: $scoreAsJson")
            println("Patch: $currentDiff")

            currentDiff = JSON.stringify(patchOperations)
        }

        return currentDiff
    }
}