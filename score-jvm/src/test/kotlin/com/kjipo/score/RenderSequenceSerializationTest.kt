package com.kjipo.score

import com.kjipo.handler.ScoreHandler
import com.kjipo.svg.*
import kotlinx.serialization.internal.DoubleSerializer
import kotlinx.serialization.json.JSON
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import org.junit.Test


class RenderSequenceSerializationTest {


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

                note {
                    note = NoteType.C
                    octave = 5
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


    @Test
    fun listSerializationTest() {
        val testList = listOf(1.0, 2.0, 3.0)

//        val scope = SerialContext()
//        scope.registerSerializer(Double::class, Double::class.serializer())
//        scope.registerSerializer(List::class, Double::class.serializer().list)


        val jsonData = JSON.stringify(DoubleSerializer.list, testList)

        println("jsonData: $jsonData")
    }


    @Test
    fun pathElementSerializationTest() {
        val pathElement = PathElement(PathCommand.MOVE_TO_ABSOLUTE,
                listOf(1.0, 2.0, 3.0))

        val jsonData = JSON.stringify(pathElement)

        println("jsonData: $jsonData")
    }

    @Test
    fun glyphDataSerializationTest() {
        val pathElement = PathElement(PathCommand.MOVE_TO_ABSOLUTE,
                listOf(1.0, 2.0, 3.0))

        val pathElement2 = PathElement(PathCommand.MOVE_TO_RELATIVE,
                listOf(4.0, 5.0, 6.0))

        val glyphData = GlyphData("Test", listOf(pathElement, pathElement2),
                1, BoundingBox(0.0, 0.0, 100.0, 100.0))


        val jsonData = JSON.stringify(glyphData)

        println("jsonData: $jsonData")


        val deserializedRenderedSequence = JSON.parse<GlyphData>(jsonData)

        println("Deserialized: $deserializedRenderedSequence")

    }

    @Test
    fun renderingElementImplSerializationTest() {
        val pathElement = PathElement(PathCommand.MOVE_TO_ABSOLUTE,
                listOf(1.0, 2.0, 3.0))
        val pathElement2 = PathElement(PathCommand.MOVE_TO_RELATIVE,
                listOf(4.0, 5.0, 6.0))
        val glyphData = GlyphData("Test", listOf(pathElement, pathElement2),
                1, BoundingBox(0.0, 0.0, 100.0, 100.0))
        val pathInterfaceImpl = PathInterfaceImpl(
                listOf(pathElement, pathElement2),
                2)

        val renderingElement = PositionedRenderingElement.create(listOf(pathInterfaceImpl),
                BoundingBox(0.0, 0.0, 100.0, 100.0),
                "note-1",
                0, 0)

        val jsonData = JSON.stringify(renderingElement)

        println("jsonData: $jsonData")

        val deserializedRenderedSequence = JSON.parse<PositionedRenderingElement>(jsonData)

        println("Deserialized: $deserializedRenderedSequence")

    }


    @Test
    fun pathInterfaceImplSerializationTest() {
        val pathElement = PathElement(PathCommand.MOVE_TO_ABSOLUTE,
                listOf(1.0, 2.0, 3.0))

        val pathElement2 = PathElement(PathCommand.MOVE_TO_RELATIVE,
                listOf(4.0, 5.0, 6.0))


        val pathInterfaceImpl = PathInterfaceImpl(
                listOf(pathElement, pathElement2),
                2)


        val jsonData = JSON.stringify(PathInterfaceImpl::class.serializer().list, listOf(pathInterfaceImpl))

        println("jsonData: $jsonData")

    }


}