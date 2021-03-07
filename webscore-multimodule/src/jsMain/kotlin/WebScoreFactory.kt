import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreHandlerWithReducedLogic
import com.kjipo.handler.ScoreHandlerWithState

object WebScoreFactory {

    @JsName("createScoreHandler")
    fun createScoreHandler() = ScoreHandlerJavaScript(ScoreHandler())

    @JsName("createWebScore")
    fun createWebScore(scoreHandlerJavaScript: ScoreHandlerJavaScript, svgElementName: String = "score") = WebScore(scoreHandlerJavaScript, svgElementName)

    @JsName("createScoreHandlerWithState")
    fun createWebscoreHandlerWithState() = ScoreHandlerWithStateImpl(ScoreHandler())

    @JsName("createWebscoreHandlerStateBackend")
    fun createWebscoreHandlerStateBackend(scoreHandlerWithState: ScoreHandlerWithState) = WebScoreScoreHandlerStateBackend(scoreHandlerWithState)

    @JsName("createScoreManipulationInterface")
    fun createScoreManipulationInterface(scoreHandlerWithState: ScoreHandlerWithState) = ScoreHandlerWithStateJavaScript(scoreHandlerWithState)

}
