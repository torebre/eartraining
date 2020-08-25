import com.kjipo.handler.ScoreHandler
import kotlin.test.Test


class DiffTests {

    @Test
    fun testDiff() {
        val scoreHandler = ScoreHandler()
        val scoreHandlerWithState = ScoreHandlerWithStateImpl(scoreHandler)
        val webScore = WebScoreScoreHandlerStateBackend(scoreHandlerWithState)


    }



}