import com.kjipo.handler.Score
import com.kjipo.handler.ScoreElementsTranslator
import com.kjipo.handler.ScoreHandlerWithReducedLogic
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import com.kjipo.scoregenerator.ReducedScore

// Uncommenting the following will set up an empty webscore automatically when the Javascript in the built module is run
//val scoreHandler = ScoreHandlerJavaScript(ScoreHandler())
//val intialWebscore = WebScore(scoreHandler)



fun main() {
    val noteSequence = listOf(NoteSequenceElement.NoteElement("test1", NoteType.G, 5, Duration.QUARTER))
    val score = ScoreElementsTranslator.createRenderingData(noteSequence)
    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")

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
