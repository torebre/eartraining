package com.kjipo.score

import com.kjipo.handler.ScoreHandler
import kotlinx.serialization.json.JSON
import kotlin.test.Test


class SerializationTest {

    @Test
    fun serializeRenderingSequence() {
        val scoreHandler = ScoreHandler()

        scoreHandler.insertNote(Duration.QUARTER, 4, NoteType.A)
        scoreHandler.insertNote(Duration.QUARTER, 4, NoteType.H)
        scoreHandler.insertNote(Duration.QUARTER, 4, NoteType.C)
        scoreHandler.insertRest(Duration.QUARTER)

        val jsonData = scoreHandler.getScoreAsJson()

        println("jsonData: $jsonData")

        val deserializedRenderedSequence = JSON.parse(RenderingSequence.serializer(), jsonData)

        println("Deserialized: $deserializedRenderedSequence")
    }


}