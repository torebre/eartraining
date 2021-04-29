import com.kjipo.handler.*
import com.kjipo.score.*
import com.kjipo.scoregenerator.ELEMENT_ID
import mu.KotlinLogging
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel

// Uncommenting the following will set up an empty webscore automatically when the Javascript in the built module is run
//val scoreHandler = ScoreHandlerJavaScript(ScoreHandler())
//val intialWebscore = WebScore(scoreHandler)


private val logger = KotlinLogging.logger {}

fun main() {
    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    val bar = Bar().also {
        it.clef = Clef.G
        it.timeSignature = TimeSignature(4, 4)
    }

    val note1 = Note("test1", Duration.QUARTER, 5, NoteType.A_SHARP)
    val note2 = Note("test2", Duration.QUARTER, 5, NoteType.A)
    val notes = listOf(note1, note2)

    bar.scoreHandlerElements.addAll(notes)

    val score = Score()
    score.bars.add(bar)

    score.ties.add(Pair(note1, note2))

    val scoreHandler = ScoreHandlerWithReducedLogic(score)

    val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
}
