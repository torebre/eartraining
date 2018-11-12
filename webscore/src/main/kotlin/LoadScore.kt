import com.kjipo.handler.ScoreHandler
import com.kjipo.score.*
import kotlinx.serialization.json.JSON
import kotlin.browser.window



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
            note = NoteType.D
            duration = Duration.QUARTER
            octave = 4
        }

        note {
            note = NoteType.G
            duration = Duration.QUARTER
        }

        rest {
            duration = Duration.QUARTER
        }
    }
})

//val webScore = WebScore(scoreHandler)

//fun loadJson(serializedRenderSequence: String) {
//    val deserializedRenderedSequence = JSON.parse<RenderingSequence>(serializedRenderSequence)
//
//    scoreHandler.currentScore = deserializedRenderedSequence
//    webScore.reload()
//}