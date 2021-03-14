package com.kjipo.scoregenerator

import com.kjipo.handler.NoteOrRest
import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SequenceGeneratorTest {

    @Test
    fun `Elements are added to underlying score`() {
        val simpleNoteSequence = SimpleSequenceGenerator.createSequence()
        val sequenceGenerator = SequenceGenerator()
        sequenceGenerator.loadSimpleNoteSequence(simpleNoteSequence)
        val notes =
            sequenceGenerator.scoreHandler.getScoreHandlerElements().filter { it is NoteOrRest && it.isNote }.toList()

        assertFalse { notes.isEmpty() }

        println("Score as JSON: ${sequenceGenerator.scoreHandler.getScoreAsJson()}")
    }

    @Test
    fun `Elements show up in score output`() {
        val sequenceGenerator = SequenceGenerator()
        val noteSequence = listOf<NoteSequenceElement>(NoteSequenceElement.NoteElement(NoteType.C, 5, Duration.QUARTER))
        sequenceGenerator.loadSimpleNoteSequence(SimpleNoteSequence(noteSequence))
        val scoreAsJson = sequenceGenerator.scoreHandler.getScoreAsJson()

        assertTrue(scoreAsJson.contains("QUARTER"))
    }

}