import com.kjipo.handler.ScoreHandler
import com.kjipo.score.*
import kotlinx.serialization.json.JSON
import kotlin.browser.window



val scoreHandler = ScoreHandlerJavaScript(ScoreHandler {
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
})

//val webScore = WebScore(scoreHandler)

//fun loadJson(serializedRenderSequence: String) {
//    val deserializedRenderedSequence = JSON.parse<RenderingSequence>(serializedRenderSequence)
//
//    scoreHandler.currentScore = deserializedRenderedSequence
//    webScore.reload()
//}