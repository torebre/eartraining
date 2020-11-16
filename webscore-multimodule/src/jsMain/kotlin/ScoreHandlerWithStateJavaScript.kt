import com.kjipo.handler.*
import com.kjipo.score.Accidental

class ScoreHandlerWithStateJavaScript(val scoreHandlerWithState: ScoreHandlerWithState) {

    @JsName("getScoreAsJson")
    fun getScoreAsJson() = scoreHandlerWithState.getScoreAsJson()

    @JsName("moveNoteOneStep")
    fun moveNoteOneStep(id: String, up: Boolean) = scoreHandlerWithState.applyOperation(MoveElement(id, up))

    @JsName("getIdOfFirstSelectableElement")
    fun getIdOfFirstSelectableElement() = scoreHandlerWithState.getIdOfFirstSelectableElement()

    @JsName("getNeighbouringElement")
    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreHandlerWithState.getNeighbouringElement(activeElement, lookLeft)

    @JsName("updateDuration")
    fun updateDuration(activeElement: String, keyPressed: Int) =
            scoreHandlerWithState.applyOperation(UpdateElement(activeElement, duration = ScoreHandlerUtilities.getDuration(keyPressed)))

    @JsName("insertNote")
    fun insertNote(activeElement: String, keyPressed: Int) = scoreHandlerWithState.applyOperation(InsertNote(activeElement, keyPressed))

    @JsName("insertNoteLast")
    fun insertNote(keyPressed: Int) = scoreHandlerWithState.applyOperation(InsertNote(duration = ScoreHandlerUtilities.getDuration(keyPressed)))

//    @JsName("switchBetweenNoteAndRest")
//    fun switchBetweenNoteAndRest(activeElement: String, keyPressed: Int) = scoreHandlerWithState.switchBetweenNoteAndRest(activeElement, keyPressed)

    @JsName("deleteElement")
    fun deleteElement(id: String) = scoreHandlerWithState.applyOperation(DeleteElement(id))

//    @JsName("toggleExtra")
//    fun toggleExtra(id: String, extra: Accidental) = scoreHandlerWithState.toggleExtra(id, extra)

}