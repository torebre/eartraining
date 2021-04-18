import com.kjipo.handler.ReducedScoreInterface
import com.kjipo.handler.ScoreOperation
import com.kjipo.score.Duration

class ScoreHandlerJavaScript(private val scoreHandler: ReducedScoreInterface) {

    @JsName("getScoreAsJson")
    fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    @JsName("moveNoteOneStep")
    fun moveNoteOneStep(id: String, up: Boolean) = scoreHandler.moveNoteOneStep(id, up)

    @JsName("getIdOfFirstSelectableElement")
    fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    @JsName("getNeighbouringElement")
    fun getNeighbouringElement(activeElement: String?, lookLeft: Boolean) =
        scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    @JsName("updateDuration")
    fun updateDuration(activeElement: String, duration: Duration) = scoreHandler.updateDuration(activeElement, duration)

    @JsName("deleteElement")
    fun deleteElement(id: String) = scoreHandler.deleteElement(id)

    @JsName("getHighlightMap")
    fun getHighlightMap() = scoreHandler.getHighlightElementsMap()

    @JsName("applyOperation")
    fun applyOperation(scoreOperation: ScoreOperation) = scoreHandler.applyOperation(scoreOperation)
}