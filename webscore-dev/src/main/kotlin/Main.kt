import com.kjipo.handler.ScoreHandler
import com.kjipo.score.*
import com.kjipo.scoregenerator.SimpleSequenceGenerator
import kotlin.browser.window


private fun setupTestScore2() {
    val scoreHandler = ScoreHandler()

    val note1 = scoreHandler.insertNote(Duration.EIGHT)
    val note2 = scoreHandler.insertNote(Duration.EIGHT)
    val note3 = scoreHandler.insertNote(Duration.QUARTER)
    val note4 = scoreHandler.insertNote(Duration.QUARTER)

//    scoreHandler.addBeams(listOf(note2, note3))

    WebScore(ScoreHandlerJavaScript(scoreHandler))
}


private fun setupEmptyBar() {
    val scoreHandler = ScoreHandler()

    scoreHandler.insertRest(Duration.QUARTER)
    scoreHandler.insertRest(Duration.QUARTER)
    scoreHandler.insertRest(Duration.QUARTER)
    scoreHandler.insertRest(Duration.QUARTER)

    WebScore(ScoreHandlerJavaScript(scoreHandler))
}


private fun generateTrainingSequence() {
    val sequenceGenerator = SimpleSequenceGenerator()
}


fun main() {
//    setupEmptyBar()

    console.log("Test23")

    setupTestScore2()

}
