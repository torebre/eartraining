import com.kjipo.handler.ScoreHandler
import com.kjipo.score.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlin.test.Test

class WebScoreTest {

    @ImplicitReflectionSerializer
    @Test
    fun checkLoadingScoreWorks() {
        val scoreHandler = ScoreHandler()
        scoreHandler.insertNote(Duration.HALF)
        scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(Duration.HALF)
        scoreHandler.insertNote(Duration.WHOLE)
        scoreHandler.insertNote(Duration.QUARTER)

        val webScore = WebScore(ScoreHandlerJavaScript(scoreHandler))
        webScore.highlight("note-1")

    }


}