import com.kjipo.scoregenerator.ReducedScore

object WebScoreFactory {

    @JsName("createScoreHandler")
    fun createScoreHandler() = ScoreHandlerJavaScript(ReducedScore())

    @JsName("createWebScore")
    fun createWebScore(scoreHandlerJavaScript: ScoreHandlerJavaScript, svgElementName: String = "score") = WebScore(scoreHandlerJavaScript, svgElementName)

}
