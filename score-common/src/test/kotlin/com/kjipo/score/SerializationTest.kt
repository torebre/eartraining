package com.kjipo.score

import com.kjipo.handler.ScoreHandler
import kotlinx.serialization.json.JSON
import kotlin.test.Test


class SerializationTest {

    @Test
    fun serializeRenderingSequence() {
        val scoreHandler = ScoreHandler {
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


        val jsonData = JSON.stringify(RenderingSequence.serializer(), scoreHandler.scoreData.build())

        println("jsonData: $jsonData")

        val deserializedRenderedSequence = JSON.parse(RenderingSequence.serializer(), jsonData)

        println("Deserialized: $deserializedRenderedSequence")
    }


}