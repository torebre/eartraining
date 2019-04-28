package com.kjipo.handler

import com.kjipo.score.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class ScoreHandlerTest {

    private val noteNextMapUp = mapOf(
            Pair(NoteType.A, NoteType.H),
            Pair(NoteType.H, NoteType.C),
            Pair(NoteType.C, NoteType.D),
            Pair(NoteType.D, NoteType.E),
            Pair(NoteType.E, NoteType.F),
            Pair(NoteType.F, NoteType.G),
            Pair(NoteType.G, NoteType.A)
    )

    private val noteNextMapDown = mapOf(
            Pair(NoteType.H, NoteType.A),
            Pair(NoteType.C, NoteType.H),
            Pair(NoteType.D, NoteType.C),
            Pair(NoteType.E, NoteType.D),
            Pair(NoteType.F, NoteType.E),
            Pair(NoteType.G, NoteType.F),
            Pair(NoteType.A, NoteType.G)
    )

    @Test
    fun moveNoteMoreThanOneOctaveUp() {
        val scoreHandler = ScoreHandler()

        for (i in 1..10) {
            val currentNote = scoreHandler.findScoreHandlerElement("1")!!.noteType
            scoreHandler.moveNoteOneStep("1", true)
            val newCurrentNote = scoreHandler.findScoreHandlerElement("1")!!.noteType

            assertTrue { noteNextMapUp[currentNote] == newCurrentNote }
        }
    }


//    @Test
//    fun moveNoteMoreThanOneOctaveDown() {
//        val scoreHandler = ScoreHandler {
//            bar {
//                barData.clef = Clef.G
//                barData.timeSignature = TimeSignature(4, 4)
//
//                note {
//                    note = NoteType.A
//                    duration = Duration.QUARTER
//                    octave = 4
////                    id = "note-1"
//                }
//            }
//        }
//
//        for (i in 1..10) {
//            val currentNote = scoreHandler.scoreData.findNote("note-1")!!.note
//            scoreHandler.moveNoteOneStep("note-1", false)
//            val newCurrentNote = scoreHandler.scoreData.findNote("note-1")!!.note
//
//            assertTrue { noteNextMapDown[currentNote]!!.equals(newCurrentNote) }
//        }
//    }


    @Test
    fun deleteNoteTest() {
        val scoreHandler = ScoreHandler()
        val id1 = scoreHandler.insertNote(Duration.QUARTER)
        val id2 = scoreHandler.insertNote(Duration.QUARTER)

        assertTrue {
            scoreHandler.getScoreHandlerElements().size == 2
        }

        scoreHandler.deleteElement(id1)
        assertTrue {
            scoreHandler.getScoreHandlerElements().size == 1
        }

        assertTrue {
            scoreHandler.getScoreHandlerElements()[0].id == id2
        }

    }


    @Test
    fun sequenceTest() {
        val scoreHandler = ScoreHandler()
        scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(Duration.HALF)
        scoreHandler.insertNote(Duration.WHOLE)

        // Just check that the build does not fail
        scoreHandler.build()
    }

    @Test
    fun sequenceTest2() {
        val scoreHandler = ScoreHandler()
        scoreHandler.insertNote(Duration.HALF)
        scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(Duration.HALF)
        scoreHandler.insertNote(Duration.WHOLE)
        scoreHandler.insertNote(Duration.QUARTER)

        // Just check that the build does not fail
        scoreHandler.build()
    }


    @Test
    fun insertNoteTest() {
        val scoreHandler = ScoreHandler()
        val noteId = scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(Duration.QUARTER)
        scoreHandler.insertNote(noteId, Duration.HALF)
        scoreHandler.build()

        val scoreHandlerElements = scoreHandler.getScoreHandlerElements()
        scoreHandlerElements.forEach { println(it) }

        assertEquals(scoreHandlerElements.size, 6)
        assertEquals(scoreHandlerElements[0].duration, Duration.QUARTER)
        assertEquals(scoreHandlerElements[1].duration, Duration.HALF)
        assertEquals(scoreHandlerElements[2].duration, Duration.QUARTER)
        assertEquals(scoreHandlerElements[3].duration, Duration.QUARTER)
        assertEquals(scoreHandlerElements[4].duration, Duration.QUARTER)
        assertEquals(scoreHandlerElements[5].duration, Duration.HALF)
        assertFalse(scoreHandlerElements[5].isNote)
    }

    @Test
    fun insertNoteTest2() {
        val scoreHandler = ScoreHandler()
        val noteId = scoreHandler.insertNote(Duration.QUARTER)
        val noteId2 = scoreHandler.insertNote(noteId, Duration.HALF)!!
        val noteId3 = scoreHandler.insertNote(noteId2, Duration.QUARTER)!!
        scoreHandler.insertNote(noteId3, Duration.QUARTER)
        scoreHandler.build()

        assertEquals(scoreHandler.scoreSetup.bars.size, 2)

        assertEquals(listOf(Duration.QUARTER, Duration.HALF, Duration.QUARTER), getDurationsInBar(scoreHandler.scoreSetup.bars[0].scoreRenderingElements))
        assertEquals(listOf(Duration.QUARTER, Duration.HALF, Duration.QUARTER), getDurationsInBar(scoreHandler.scoreSetup.bars[1].scoreRenderingElements))

        scoreHandler.insertNote(noteId, Duration.QUARTER)
        scoreHandler.build()

        assertEquals(scoreHandler.scoreSetup.bars.size, 2)

        assertEquals(listOf(Duration.QUARTER, Duration.QUARTER, Duration.HALF), getDurationsInBar(scoreHandler.scoreSetup.bars[0].scoreRenderingElements))
        assertEquals(listOf(Duration.QUARTER, Duration.QUARTER, Duration.HALF), getDurationsInBar(scoreHandler.scoreSetup.bars[1].scoreRenderingElements))
    }

    @Test
    fun insertNoteTest3() {
        val scoreHandler = ScoreHandler()
        val noteId = scoreHandler.insertNote(Duration.QUARTER)
        val noteId2 = scoreHandler.insertNote(noteId, Duration.QUARTER)!!
        scoreHandler.insertNote(noteId2, Duration.HALF)!!
        scoreHandler.build()

        assertEquals(1, scoreHandler.scoreSetup.bars.size)

        assertEquals(listOf(Duration.QUARTER, Duration.QUARTER, Duration.HALF), getDurationsInBar(scoreHandler.scoreSetup.bars[0].scoreRenderingElements))

        scoreHandler.insertNote(noteId2, Duration.QUARTER)
        scoreHandler.build()

        assertEquals(2, scoreHandler.scoreSetup.bars.size)

        assertEquals(listOf(Duration.QUARTER, Duration.QUARTER, Duration.QUARTER, Duration.QUARTER), getDurationsInBar(scoreHandler.scoreSetup.bars[0].scoreRenderingElements))
        assertEquals(listOf(Duration.QUARTER, Duration.HALF, Duration.QUARTER), getDurationsInBar(scoreHandler.scoreSetup.bars[1].scoreRenderingElements))
    }


    private fun getDurationsInBar(scoreRenderingElements: List<ScoreRenderingElement>): List<Duration> {
        return scoreRenderingElements.map {
            when (it) {
                is NoteElement -> it.duration
                is RestElement -> it.duration
                else -> null
            }
        }.filterNotNull().toList()

    }


}