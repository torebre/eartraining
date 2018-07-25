package com.kjipo.svg

import com.kjipo.score.*
import com.kjipo.viewer.ScoreController
import com.kjipo.viewer.startApplication
import org.junit.Test
import tornadofx.*


class GeneratedSequenceTest {

    @Test
    fun `Visualize generated sequence`() {
        val sequenceGenerator = SequenceGenerator()
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

        println(testScore.renderingElements)

//        var idCounter = 0
//        testScore.renderingElements.forEach { it.id = idCounter++ }

        startApplication()

        Thread.sleep(5000)
        println("Initialized: ${FX.initialized.value}")

        val scoreController = FX.find(ScoreController::class.java)

        FX.runAndWait { scoreController.fireLoadScore(testScore) }

        Thread.sleep(Long.MAX_VALUE)
    }




}