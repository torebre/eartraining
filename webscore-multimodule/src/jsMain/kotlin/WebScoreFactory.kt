import com.kjipo.handler.ScoreHandler
import com.kjipo.handler.ScoreHandlerWithState

object WebScoreFactory {

    @JsName("createScoreHandler")
    fun createScoreHandler() = ScoreHandlerJavaScript(ScoreHandler())

    @JsName("createWebScore")
    fun createWebScore(scoreHandlerJavaScript: ScoreHandlerJavaScript) = WebScore(scoreHandlerJavaScript)

    @JsName("createScoreHandlerWithState")
    fun createWebscoreHandlerWithState() = ScoreHandlerWithStateImpl(ScoreHandler())

    @JsName("createWebscoreHandlerStateBackend")
    fun createWebscoreHandlerStateBackend(scoreHandlerWithState: ScoreHandlerWithState) = WebScoreScoreHandlerStateBackend(scoreHandlerWithState)
}
