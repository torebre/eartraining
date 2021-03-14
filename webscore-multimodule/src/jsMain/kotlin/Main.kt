import com.kjipo.handler.ScoreHandlerInterface
import com.kjipo.handler.ScoreHandlerSplit
import com.kjipo.score.Duration
import com.kjipo.score.NoteType

// Uncommenting the following will set up an empty webscore automatically when the Javascript in the built module is run
//val scoreHandler = ScoreHandlerJavaScript(ScoreHandler())
//val intialWebscore = WebScore(scoreHandler)


fun addNoteGroup() {
    val scoreHandler = ScoreHandlerSplit()
    scoreHandler.addNoteGroup(
        Duration.QUARTER,
        listOf(
            ScoreHandlerInterface.GroupNote(NoteType.A, 5),
            ScoreHandlerInterface.GroupNote(NoteType.C_SHARP, 5),
            ScoreHandlerInterface.GroupNote(NoteType.D, 6)
        )
    )
    scoreHandler.insertNote(Duration.QUARTER)

    WebScore(ScoreHandlerJavaScript(scoreHandler))
}

fun main() {
    // Do nothing as default now

//    val scoreHandler = ScoreHandler()

//    val note1 = scoreHandler.insertNote(Duration.EIGHT)
//    val note2 = scoreHandler.insertNote(Duration.EIGHT)
//    val note3 = scoreHandler.insertNote(Duration.QUARTER)
//    val note4 = scoreHandler.insertNote(Duration.QUARTER)
//    val note5 = scoreHandler.insertNote(Duration.QUARTER)

//    WebScore(ScoreHandlerJavaScript(scoreHandler))

//    addNoteGroup()
}
