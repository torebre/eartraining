import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Duration
import kotlin.test.Test


class DiffTests {

    @Test
    fun testDiff() {
        val scoreHandler = ScoreHandler()
        val scoreHandlerWithState = ScoreHandlerWithStateImpl(scoreHandler)

        val scoreHandlerState = ScoreHandlerWithStateImpl(scoreHandler)

        val stateDiff = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))

//        println("State diff: $stateDiff")
//        val stateDiff2 = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))
//        println("State diff2: $stateDiff2")


        val webScore = WebScoreScoreHandlerStateBackend(scoreHandlerWithState)

        webScore.applyOperationAndUpdateSvg(InsertNote(duration = Duration.HALF))


    }



}