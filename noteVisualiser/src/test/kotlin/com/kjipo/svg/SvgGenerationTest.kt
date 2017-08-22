package com.kjipo.svg

import com.kjipo.viewer.ScoreController
import com.kjipo.viewer.startApplication
import tornadofx.*


fun generateSequence() {
    val testScore = createScore().score {
        bar {
            clef = Clef.G

            note {
                pitch = 60
                duration = 24
            }

            note {
                pitch = 67
                duration = 48
            }

            note {
                pitch = 80
                duration = 24
            }

            note {
                pitch = 52
                duration = 24
            }

        }

        // TODO Support multiple bars
        bar {
            note {
                pitch = 60
                duration = 24
            }

            note {
                pitch = 67
                duration = 48
            }

        }

    }

    println(testScore.renderingElements)

    var idCounter = 0
    testScore.renderingElements.forEach { it.id = idCounter++ }

//    val htmlPath = Paths.get("/home/student/workspace/EarTraining/noteVisualiser/src/main/resources/test_output3.html")
//    writeToHtmlFile(testScore, htmlPath)

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

//    scoreController.fireLoadScore(testScore)


}


fun main(args: Array<String>) {
    generateSequence()
}