package com.kjipo.scoregenerator

import com.kjipo.handler.NoteOrRest
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReducedScoreTest {

    @Test
    fun elementsAreAddedToScoreTest() {
        val simpleNoteSequence = SimpleSequenceGenerator.createSequence()
        val sequenceGenerator = ReducedScore()
        sequenceGenerator.loadSimpleNoteSequence(simpleNoteSequence)
        sequenceGenerator.getActionSequenceScript()
        val notes = sequenceGenerator.getScore()
        val noteElements =
            notes.bars.flatMap { it.scoreHandlerElements }.filter { it is NoteOrRest && it.isNote }.toList()

        assertFalse { noteElements.isEmpty() }
    }

    @Test
    fun `Elements show up in score output`() {
        val sequenceGenerator = ReducedScore()
        val noteSequence =
            listOf<NoteSequenceElement>(NoteSequenceElement.NoteElement("test1", NoteType.C, 5, Duration.QUARTER))
        sequenceGenerator.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence))
        val scoreAsJson = sequenceGenerator.getScoreAsJson()

        assertTrue(scoreAsJson.contains("QUARTER"))
    }

    @Test
    fun `Action sequence gets generated`() {
        val sequenceGenerator = ReducedScore()
        val noteSequence =
            listOf<NoteSequenceElement>(
                NoteSequenceElement.NoteElement("test1", NoteType.C, 5, Duration.QUARTER),
                NoteSequenceElement.NoteElement("test2", NoteType.D, 5, Duration.QUARTER)
            )
        sequenceGenerator.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence))
        val actionScript = sequenceGenerator.getActionSequenceScript()
        val pitchEvents =
            actionScript.timeEventList.flatMap { it.second }.filterIsInstance<Action.PitchEvent>().filter { it.noteOn }


        println("Pitch events: ${pitchEvents}")

        assertTrue(pitchEvents.size == 2)
    }

    @Test
    fun `Action sequence contains multiple note pitches`() {
        val sequenceGenerator = ReducedScore()
        val noteSequence = listOf(
            NoteSequenceElement.NoteElement("test1", NoteType.G, 5, Duration.QUARTER),
            NoteSequenceElement.MultipleNotesElement(
                "test3", listOf(
                    NoteSequenceElement.NoteElement("test1", NoteType.C, 5, Duration.QUARTER),
                    NoteSequenceElement.NoteElement("test2", NoteType.E, 5, Duration.QUARTER)
                ), Duration.QUARTER
            )
        )

        sequenceGenerator.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence))
        val actionScript = sequenceGenerator.getActionSequenceScript()
        val pitchEvents =
            actionScript.timeEventList.flatMap { it.second }.filterIsInstance<Action.PitchEvent>().filter { it.noteOn }

        assertTrue(pitchEvents.size == 2)
    }

}