import com.kjipo.handler.InsertNote
import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class DiffTests {


    @Test
    fun thingsShouldWork() {
        assertEquals(listOf(1,2,3).reversed(), listOf(3,2,1))
    }

    @Test
    fun testDiff() {
        val scoreHandler = ScoreHandler()
        val scoreHandlerWithState = ScoreHandlerWithStateImpl(scoreHandler)

        val scoreHandlerState = ScoreHandlerWithStateImpl(scoreHandler)

        val stateDiff = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))

        println("State diff: $stateDiff")
        val stateDiff2 = scoreHandlerState.applyOperation(InsertNote(duration = Duration.QUARTER))
        println("State diff2: $stateDiff2")

        val webScore = WebScoreScoreHandlerStateBackend(scoreHandlerWithState)

        webScore.applyOperationAndUpdateSvg(InsertNote(duration = Duration.HALF))


    }



}