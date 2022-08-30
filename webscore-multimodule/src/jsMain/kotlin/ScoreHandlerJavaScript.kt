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

//    @JsName("updateDuration")
//    fun updateDuration(activeElement: String, duration: Duration) {
//        scoreHandler.updateDuration(activeElement, duration)
//        fireListeners()
//    }

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

    @JsName("addListener")
    fun addListener(scoreHandlerListener: ScoreHandlerListener) = listeners.add(scoreHandlerListener)

    @JsName("removeListener")
    fun removeListener(scoreHandlerListener: ScoreHandlerListener) = listeners.remove(scoreHandlerListener)

    private fun fireListeners() {
        listeners.forEach { it.scoreUpdated() }
    }

}