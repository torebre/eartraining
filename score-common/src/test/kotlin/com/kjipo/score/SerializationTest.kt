package com.kjipo.score

import com.kjipo.handler.ScoreHandler
import kotlinx.serialization.json.JSON
import kotlin.test.Test


class SerializationTest {

    @Test
    fun serializeRenderingSequence() {
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


//        val scope = SerialContext()
//        scope.registerSerializer(Double::class, Double::class.serializer())
//        scope.registerSerializer(List::class., Double::class.serializer().list)


        val jsonData = JSON.stringify(scoreHandler.currentScore)


        println("jsonData: $jsonData")

        val deserializedRenderedSequence = JSON.parse<RenderingSequence>(jsonData)

        println("Deserialized: $deserializedRenderedSequence")


    }


}