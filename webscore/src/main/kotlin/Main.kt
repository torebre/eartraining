import com.kjipo.handler.ScoreHandler
import com.kjipo.score.*

fun main(args: Array<String>) {
    println("Hello, browser!")

    val scoreData = ScoreSetup()
    var idCounter = 0
    val note1 = NoteElement(NoteType.C, 5, Duration.HALF, 0, 0, 0, "note-$idCounter")
    ++idCounter
    val note2 = NoteElement(NoteType.D, 5, Duration.HALF, 0, 0, 0, "note-$idCounter", tie = note1.id)
    ++idCounter

    scoreData.noteElements.add(note1)
    scoreData.noteElements.add(note2)

    val barData = BarData()
    barData.clef = Clef.G
    barData.scoreRenderingElements.add(note1)
    barData.scoreRenderingElements.add(note2)

    scoreData.bars.add(barData)

    scoreData.test.add(21)

    WebScore(ScoreHandlerJavaScript(ScoreBuilderSequence(scoreData)))


//    val scoreHandler = ScoreHandlerJavaScript(ScoreHandler {
//        bar {
//            barData.clef = Clef.G
//            barData.timeSignature = TimeSignature(4, 4)
//
//            note {
//                note = NoteType.A
//                duration = Duration.QUARTER
//                octave = 4
//            }
//
//            note {
//                note = NoteType.E
//                duration = Duration.QUARTER
//                octave = 5
//            }
//
//            note {
//                note = NoteType.C
//                duration = Duration.QUARTER
//            }
//
//            rest {
//                duration = Duration.QUARTER
//            }
//        }
//    })
//
//
//    WebScore(scoreHandler)
}
