import com.kjipo.score.*
import kotlin.browser.document
import kotlin.browser.window



fun main(args: Array<String>) {
    val testScore = createScore().score {
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

//    val testScore2 = createScore().score {
//        bar {
//            clef = Clef.G
//            timeSignature = TimeSignature(4, 4)
//
//            note {
//                note = NoteType.A
//                duration = Duration.QUARTER
//                octave = 4
//            }
//
//            note {
//                note = NoteType.D
//                duration = Duration.QUARTER
//                octave = 4
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
//
//        }
//
//    }



    var webScore:WebScore

    window.onload = {
        webScore = WebScore(testScore)
        webScore.highlight("note-1")


        print("Called webscore highlight")

        val snap = Snap("#note-1")
        snap.transform("t100,100")


        webScore.move("note-1", 10)

        webScore

    }

//    js("sleep(5000);")



    window.setTimeout({
        val snap = Snap("#note-1")
        snap.transform("t200,200")

    }, 5000)







}


