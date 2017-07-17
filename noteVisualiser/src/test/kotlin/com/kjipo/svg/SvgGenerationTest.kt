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
                    pitch = 62
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


        val path = Paths.get("/home/student/test_output3.xml")

//    writeToFile(temporalElementSequence, path)

        writeToFile(testScore, path)


    }


}