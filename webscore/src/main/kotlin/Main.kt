import com.kjipo.handler.ScoreHandler
import com.kjipo.score.Clef
import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TimeSignature

fun main(args: Array<String>) {
    println("Hello, browser!")


    val scoreHandler = ScoreHandlerJavaScript(ScoreHandler {
        bar {
            barData.clef = Clef.G
            barData.timeSignature = TimeSignature(4, 4)

            note {
                note = NoteType.A
                duration = Duration.QUARTER
                octave = 4
            }

            note {
                note = NoteType.F
                duration = Duration.QUARTER
                octave = 5
            }

            note {
                note = NoteType.C
                duration = Duration.QUARTER
            }

            rest {
                duration = Duration.QUARTER
            }
        }
    })


    WebScore(scoreHandler)
}
