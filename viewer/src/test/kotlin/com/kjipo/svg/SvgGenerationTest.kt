package com.kjipo.svg


import com.kjipo.score.*
import com.kjipo.viewer.ScoreController
import com.kjipo.viewer.startApplication
import tornadofx.*
import java.nio.file.Paths


fun generateSequence() {
    val testScore = createScore().score {
        bar {
            clef = Clef.G
            timeSignature = TimeSignature(4, 4)

            note {
                note = NoteType.C
                duration = Duration.QUARTER
                beamGroup = 1
            }

            note {
                note = NoteType.H
                duration = Duration.QUARTER
                beamGroup = 1
            }

            note {
                note = NoteType.C
                octave = 7
                duration = Duration.QUARTER
            }

            note {
                note = NoteType.C
                octave = 4
                duration = Duration.QUARTER
            }

        }

        bar {
            note {
                note = NoteType.C
                duration = Duration.HALF
            }

            note {
                note = NoteType.F
                duration = Duration.QUARTER
            }

        }

    }

    println(testScore.renderingElements)

    var idCounter = 0
    testScore.renderingElements.forEach { it.id = idCounter++ }

    val htmlPath = Paths.get("test_output.html")
    writeToHtmlFile(testScore, htmlPath)

    startApplication()

    Thread.sleep(5000)
    println("Initialized: ${FX.initialized.value}")

    val scoreController = FX.find(ScoreController::class.java)

    FX.runAndWait { scoreController.fireLoadScore(testScore) }

    Thread.sleep(500)
    FX.runAndWait { scoreController.fireNoteOn(2) }
    Thread.sleep(500)
    FX.runAndWait { scoreController.fireNoteOn(1) }
    Thread.sleep(500)
    FX.runAndWait { scoreController.fireNoteOff(2) }

    println("ScoreController: $scoreController")


}


fun main(args: Array<String>) {
    generateSequence()
}