import com.kjipo.handler.ScoreHandlerSplit
import com.kjipo.handler.ScoreHandlerWithState

object WebScoreFactory {

    @JsName("createScoreHandler")
    fun createScoreHandler() = ScoreHandlerJavaScript(ScoreHandlerSplit())

    @JsName("createWebScore")
    fun createWebScore(scoreHandlerJavaScript: ScoreHandlerJavaScript, svgElementName: String = "score") = WebScore(scoreHandlerJavaScript, svgElementName)

    @JsName("createScoreHandlerWithState")
    fun createWebscoreHandlerWithState() = ScoreHandlerWithStateImpl(ScoreHandlerSplit())

    @JsName("createWebscoreHandlerStateBackend")
    fun createWebscoreHandlerStateBackend(scoreHandlerWithState: ScoreHandlerWithState) = WebScoreScoreHandlerStateBackend(scoreHandlerWithState)

    @JsName("createScoreManipulationInterface")
    fun createScoreManipulationInterface(scoreHandlerWithState: ScoreHandlerWithState) = ScoreHandlerWithStateJavaScript(scoreHandlerWithState)

}
