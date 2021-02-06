import com.kjipo.handler.ScoreHandlerInterface
import com.kjipo.score.Accidental

class ScoreHandlerJavaScript(private val scoreHandler: ScoreHandlerInterface) {

    @JsName("getScoreAsJson")
    fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    @JsName("moveNoteOneStep")
    fun moveNoteOneStep(id: String, up: Boolean) = scoreHandler.moveNoteOneStep(id, up)

    @JsName("getIdOfFirstSelectableElement")
    fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    @JsName("getNeighbouringElement")
    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) =
        scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    @JsName("updateDuration")
    fun updateDuration(activeElement: String, keyPressed: Int) = scoreHandler.updateDuration(activeElement, keyPressed)

    @JsName("insertNote")
    fun insertNote(activeElement: String, keyPressed: Int) = scoreHandler.insertNote(activeElement, keyPressed)

    @JsName("insertNoteLast")
    fun insertNote(keyPressed: Int) = scoreHandler.insertNote(keyPressed)

    @JsName("switchBetweenNoteAndRest")
    fun switchBetweenNoteAndRest(activeElement: String, keyPressed: Int) =
        scoreHandler.switchBetweenNoteAndRest(activeElement, keyPressed)

    @JsName("deleteElement")
    fun deleteElement(id: String) = scoreHandler.deleteElement(id)

    @JsName("toggleExtra")
    fun toggleExtra(id: String, extra: Accidental) = scoreHandler.toggleExtra(id, extra)

//    @JsName("getClientContext")
//    fun getClientContext() = scoreHandler.getClientContext()

    @JsName("getHighlightMap")
    fun getHighlightMap() = scoreHandler.getHighlightElementsMap()
}