package com.kjipo.handler

import com.kjipo.score.Clef
import com.kjipo.score.Duration
import com.kjipo.score.NoteType
import com.kjipo.score.TimeSignature
import kotlin.test.Test
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

}