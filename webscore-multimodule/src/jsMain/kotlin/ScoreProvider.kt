import com.kjipo.handler.ScoreProviderInterface


class ScoreProvider(val scoreHandlerJavaScript: ScoreHandlerJavaScript): ScoreProviderInterface {

    override fun getScoreAsJson() = scoreHandlerJavaScript.getScoreAsJson()

    override fun getHighlightMap() = scoreHandlerJavaScript.getHighlightMap()

}