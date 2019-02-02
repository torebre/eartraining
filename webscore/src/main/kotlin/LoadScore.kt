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


val scoreHandler2 = ScoreHandlerJavaScript({
    val scoreData = ScoreSetup()
    var idCounter = 0
    val note1 = NoteElement(NoteType.C, 5, Duration.HALF, "note-$idCounter")
    ++idCounter
    val note2 = NoteElement(NoteType.C, 5, Duration.HALF, "note-$idCounter")
    ++idCounter

    scoreData.noteElements.add(note1)
    scoreData.noteElements.add(note2)

    val barData = BarData()
    barData.clef = Clef.G
    barData.scoreRenderingElements.add(note1)
    barData.scoreRenderingElements.add(note2)

    scoreData.bars.add(barData)

    ScoreBuilderSequence(scoreData)
}())