package com.kjipo.svg

import org.junit.Test
import java.nio.file.Paths

class SvgGenerationTest {


    @Test
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

        }


        println(testScore.renderingElements)

        var idCounter = 0
        testScore.renderingElements.forEach { it.id = idCounter++}

//        val path = Paths.get("/home/student/test_output3.xml")
//        writeToFile(testScore, path)

        val htmlPath = Paths.get("/home/student/workspace/EarTraining/noteVisualiser/src/main/resources/test_output3.html")
        writeToHtmlFile(testScore, htmlPath)

    }


}