import com.kjipo.handler.ScoreHandlerInterface
import kotlinx.html.injector.injectTo

class ScoreHandlerJavaScript(val scoreHandler: ScoreHandlerInterface) {

    @JsName("getScoreAsJson")
    fun getScoreAsJson() = scoreHandler.getScoreAsJson()

    @JsName("moveNoteOneStep")
    fun moveNoteOneStep(id: String, up: Boolean) = scoreHandler.moveNoteOneStep(id, up)

    @JsName("getIdOfFirstSelectableElement")
    fun getIdOfFirstSelectableElement() = scoreHandler.getIdOfFirstSelectableElement()

    @JsName("getNeighbouringElement")
    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean) = scoreHandler.getNeighbouringElement(activeElement, lookLeft)

    @JsName("updateDuration")
    fun updateDuration(activeElement: String, keyPressed: Int) = scoreHandler.updateDuration(activeElement, keyPressed)

    @JsName("insertNote")
    fun insertNote(activeElement: String, keyPressed: Int) = scoreHandler.insertNote(activeElement, keyPressed)

    @JsName("switchBetweenNoteAndRest")
    fun switchBetweenNoteAndRest(activeElement: String, keyPressed: Int) = scoreHandler.switchBetweenNoteAndRest(activeElement, keyPressed)

    @JsName("deleteElement")
    fun deleteElement(id: String) = scoreHandler.deleteElement(id)
}