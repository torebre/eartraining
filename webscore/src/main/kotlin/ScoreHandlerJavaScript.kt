import com.kjipo.handler.ScoreHandlerInterface

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
    fun updateDuration(id: String, keyPressed:Int) = scoreHandler.updateDuration(id, keyPressed)

}