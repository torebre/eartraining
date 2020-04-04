import com.kjipo.handler.ScoreHandler

object WebScoreFactory {

    @JsName("createScoreHandler")
    fun createScoreHandler() = ScoreHandlerJavaScript(ScoreHandler())

    @JsName("createWebScore")
    fun createWebScore(scoreHandlerJavaScript: ScoreHandlerJavaScript) = WebScore(scoreHandlerJavaScript)

}
