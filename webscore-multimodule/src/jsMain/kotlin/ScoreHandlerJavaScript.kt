import com.kjipo.handler.ReducedScoreInterface
import com.kjipo.handler.PitchSequenceOperation

class ScoreHandlerJavaScript(private val scoreHandler: ReducedScoreInterface) {

    private val listeners = mutableListOf<ScoreHandlerListener>()

    @JsName("getScoreAsJson")
    fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    @JsName("moveNoteOneStep")
    fun moveNoteOneStep(id: String, up: Boolean) {
        scoreHandler.moveNoteOneStep(id, up)
        fireListeners()
    }

    @JsName("getIdOfFirstSelectableElement")
    fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    @JsName("getNeighbouringElement")
    fun getNeighbouringElement(activeElement: String?, lookLeft: Boolean) =
        scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    @JsName("deleteElement")
    fun deleteElement(id: String) {
        scoreHandler.deleteElement(id)
        fireListeners()
    }

    @JsName("getHighlightMap")
    fun getHighlightMap() = scoreHandler.getHighlightElementsMap()

    @JsName("applyOperation")
    fun applyOperation(pitchSequenceOperation: PitchSequenceOperation) {
        scoreHandler.applyOperation(pitchSequenceOperation)
        fireListeners()
    }

    fun getLatestId() = scoreHandler.getLatestId()
    fun getChangeSet(scoreId: Int) = scoreHandler.getChangeSet(scoreId)

    @JsName("addListener")
    fun addListener(scoreHandlerListener: ScoreHandlerListener) = listeners.add(scoreHandlerListener)

    @JsName("removeListener")
    fun removeListener(scoreHandlerListener: ScoreHandlerListener) = listeners.remove(scoreHandlerListener)

    private fun fireListeners() {
        scoreHandler.getLatestId().let { latestId ->
            listeners.forEach { it.scoreUpdated(latestId) }
        }
    }

}