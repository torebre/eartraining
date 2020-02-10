import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Duration

fun main() {
    val scoreHandler = ScoreHandler()

    val note1 = scoreHandler.insertNote(Duration.EIGHT)
    val note2 = scoreHandler.insertNote(Duration.EIGHT)
    val note3 = scoreHandler.insertNote(Duration.QUARTER)
    val note4 = scoreHandler.insertNote(Duration.QUARTER)
    val note5 = scoreHandler.insertNote(Duration.QUARTER)

    WebScore(ScoreHandlerJavaScript(scoreHandler))

}
