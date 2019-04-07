package com.kjipo.svg

import com.kjipo.score.*
import com.kjipo.viewer.ScoreController
import com.kjipo.viewer.WebViewApplication.Companion.startApplication
import org.junit.Test
import tornadofx.*
import java.nio.file.Paths


class GeneratedSequenceTest {

    @Test
    fun `Visualize generated sequence`() {
        val sequenceGenerator = com.kjipo.scoregenerator.SequenceGenerator()
        val renderingSequence = sequenceGenerator.createNewSequence(true)

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val scoreController = FX.find(ScoreController::class.java)

        FX.runAndWait { scoreController.fireLoadScore(renderingSequence) }


        Thread.sleep(Long.MAX_VALUE)
    }


    @Test
    fun `Visualize scale`() {
        val testScore = createScore().score {
            bar {
                barData.clef = Clef.G
                barData.timeSignature = TimeSignature(4, 4)

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

                note {
                    note = NoteType.D
                    duration = Duration.QUARTER
                }

                note {
                    note = NoteType.E
                    duration = Duration.QUARTER
                }

                note {
                    note = NoteType.F
                    duration = Duration.QUARTER
                }

                note {
                    note = NoteType.G
                    duration = Duration.QUARTER
                }

                note {
                    note = NoteType.A
                    duration = Duration.QUARTER
                }

                note {
                    note = NoteType.H
                    duration = Duration.QUARTER
                }

                note {
                    note = NoteType.C
                    duration = Duration.QUARTER
                    octave = 6
                }

                note {
                    note = NoteType.D
                    duration = Duration.QUARTER
                    octave = 6
                }
            }

        }

//        println(testScore.renderingElements)
//        println(createHtmlDocumentString(testScore))

        val htmlPath = Paths.get("test_output.html")
        writeToHtmlFile(testScore, htmlPath)

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val scoreController = FX.find(ScoreController::class.java)

        FX.runAndWait { scoreController.fireLoadScore(testScore) }

        Thread.sleep(Long.MAX_VALUE)
    }

    @Test
    fun `Shows rests`() {
        val testScore = createScore().score {
            bar {
                barData.clef = Clef.G
                barData.timeSignature = TimeSignature(4, 4)

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

//        var idCounter = 0
//        testScore.renderingElements.forEach { it.id = idCounter++ }

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val scoreController = FX.find(ScoreController::class.java)

        FX.runAndWait { scoreController.fireLoadScore(testScore) }

        Thread.sleep(Long.MAX_VALUE)
    }


    @Test
    fun `Tie test`() {
        val scoreData = ScoreSetup()
        var idCounter = 0
        val note1 = NoteElement(NoteType.C, 5, Duration.HALF, "note-$idCounter")
        ++idCounter
        val note2 = NoteElement(NoteType.C, 5, Duration.HALF, "note-$idCounter")
        note2.tie = note1.id
        ++idCounter

        scoreData.noteElements.add(note1)
        scoreData.noteElements.add(note2)

        val barData = BarData()
        barData.clef = Clef.G
        barData.scoreRenderingElements.add(note1)
        barData.scoreRenderingElements.add(note2)

        scoreData.bars.add(barData)


        println("Score data: ${scoreData.build()}")

//        val htmlPath = Paths.get("test_output.html")
//        writeToHtmlFile(scoreData.build(), htmlPath)

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val scoreController = FX.find(ScoreController::class.java)

        FX.runAndWait { scoreController.fireLoadScore(scoreData.build()) }

        Thread.sleep(Long.MAX_VALUE)

    }


}