import com.kjipo.handler.ScoreHandler
import com.kjipo.score.*
import kotlinx.serialization.json.JSON
import kotlin.browser.window



val scoreHandler = ScoreHandler {}
val webScore = WebScore(scoreHandler)

fun loadJson(serializedRenderSequence: String) {
    val deserializedRenderedSequence = JSON.parse<RenderingSequence>(serializedRenderSequence)

    scoreHandler.currentScore = deserializedRenderedSequence
    webScore.reload()
}


fun main(args: Array<String>) {
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



//    window.onload = {
//        val webScore = WebScore(scoreHandler)
//        webScore.highlight("note-1")
//
//        print("Called webscore highlight")
//
////        val snap = Snap("#note-1")
////        snap.transform("t100,100")
//
//
////        webScore.move("note-1", 10)
//
//        webScore
//
//    }

//    js("sleep(5000);")



//    window.setTimeout({
//        val snap = Snap("#note-1")
//        snap.transform("t200,200")
//
//    }, 5000)







}


