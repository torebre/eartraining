import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Clef
import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TimeSignature
import kotlin.test.Test

class WebScoreTest {

    @Test
    fun checkLoadingScoreWorks() {
        val scoreHandler = ScoreHandler {
            bar {
                clef = Clef.G
                timeSignature = TimeSignature(4, 4)

                note {
                    note = NoteType.A
                    duration = Duration.QUARTER
                    octave = 4
                }

                note {
                    note = NoteType.H
                    duration = Duration.QUARTER
                    octave = 4
                }

                note {
                    note = NoteType.C
                    duration = Duration.QUARTER
                }

                rest {
                    duration = Duration.QUARTER
                }
            }
        }

        val webScore = WebScore(ScoreHandlerJavaScript(scoreHandler))
        webScore.highlight("note-1")

    }


}