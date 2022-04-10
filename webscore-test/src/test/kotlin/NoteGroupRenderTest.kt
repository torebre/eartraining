import com.kjipo.handler.*
import com.kjipo.score.Clef
import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TimeSignature
import mu.KotlinLoggingConfiguration
import mu.KotlinLoggingLevel
import kotlin.test.Test

class NoteGroupRenderTest {

    @Test
    fun showChordWithTwoSharps() {
        KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

        val bar = Bar().also {
            it.clef = Clef.G
            it.timeSignature = TimeSignature(4, 4)
        }

        val note1 = NoteSymbol("test1", Duration.QUARTER, 5, NoteType.A_SHARP)
        val note2 = NoteSymbol("test2", Duration.QUARTER, 6, NoteType.C_SHARP)
        val notes = listOf(note1, note2)

        val noteGroup = NoteGroup("testGroup", notes)

        bar.scoreHandlerElements.addAll(setOf(noteGroup))

        val score = Score()
        score.bars.add(bar)

        val scoreHandler = ScoreHandlerWithReducedLogic(score)

        val webScore = WebScoreView(WebscoreSvgProvider(scoreHandler), "score")
    }


}