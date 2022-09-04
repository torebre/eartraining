package com.kjipo.scoregenerator

import com.kjipo.handler.Note
import com.kjipo.handler.ScoreHandlerUtilities
import com.kjipo.handler.UpdateElement
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import mu.KotlinLogging
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReducedScoreTest {

    private val logger = KotlinLogging.logger {}

    @Test
    fun elementsAreAddedToScoreTest() {
        val simpleNoteSequence = SimpleSequenceGenerator.createSequence()
        val sequenceGenerator = ReducedScore()
        sequenceGenerator.loadSimpleNoteSequence(simpleNoteSequence)
        sequenceGenerator.getActionSequenceScript()
        val notes = sequenceGenerator.getScore()
        val noteElements =
            notes.bars.flatMap { it.scoreHandlerElements }.filterIsInstance<Note>().toList()

        assertFalse { noteElements.isEmpty() }
    }

    @Test
    fun elementsShowUpInScoreOutput() {
        val sequenceGenerator = ReducedScore()
        val noteSequence =
            listOf<NoteSequenceElement>(NoteSequenceElement.NoteElement("test1", NoteType.C, 5, Duration.QUARTER))
        sequenceGenerator.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence))
        val scoreAsJson = sequenceGenerator.getScoreAsJson()

        assertTrue(scoreAsJson.contains("QUARTER"))
    }

    @Test
    fun actionSequenceGetRendered() {
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
    fun actionSequenceContainsMultipleNotePitches() {
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

    @Test
    fun getChangeSetTest() {
        val reducedScore = ReducedScore()
        val noteSequence = listOf(
            NoteSequenceElement.NoteElement("test1", NoteType.G, 5, Duration.QUARTER),
            NoteSequenceElement.MultipleNotesElement(
                "test3", listOf(
                    NoteSequenceElement.NoteElement("test3", NoteType.C, 5, Duration.QUARTER),
                    NoteSequenceElement.NoteElement("test4", NoteType.E, 5, Duration.QUARTER)
                ), Duration.QUARTER
            )
        )

        assertEquals(0, reducedScore.getLatestId())
        reducedScore.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence))
        reducedScore.applyOperation(UpdateElement("test1", ScoreHandlerUtilities.getPitch(NoteType.G, 5) + 1))
        assertEquals(1, reducedScore.getLatestId())

        logger.info { reducedScore.getChangeSet(0) }






    }

}