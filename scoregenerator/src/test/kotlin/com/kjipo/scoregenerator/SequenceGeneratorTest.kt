package com.kjipo.scoregenerator


import com.kjipo.score.Duration
import com.kjipo.score.NoteElement
import com.kjipo.score.NoteType
import junit.framework.TestCase.*
import org.junit.Test


class SequenceGeneratorTest {


    @Test
    fun scoregeneratorTest() {
        val sequenceGenerator = SequenceGenerator()
        sequenceGenerator.createNewSequence()
        val firstElement = sequenceGenerator.scoreBuilder.scoreData.noteElements.first()
        val lengthBeforeInsertion = sequenceGenerator.scoreBuilder.scoreData.noteElements.size
        val pitchesBeforeInsertion = sequenceGenerator.pitchSequence.size

        sequenceGenerator.insertNote(firstElement.id, 1)

        assertEquals(lengthBeforeInsertion + 1, sequenceGenerator.scoreBuilder.scoreData.noteElements.size)
        assertEquals(pitchesBeforeInsertion + 1, sequenceGenerator.pitchSequence.size)
    }


    @Test
    fun `Generated simple note sequence is loaded`() {
        val generator = SimpleSequenceGenerator.createSequence()
        val sequenceGenerator = SequenceGenerator()
        sequenceGenerator.loadSimpleNoteSequence(generator)

        assertFalse(sequenceGenerator.pitchSequence.isEmpty())

        val renderingSequence = sequenceGenerator.scoreBuilder.build()

        assertTrue(renderingSequence.renderGroups.isNotEmpty())
        assertTrue(renderingSequence.renderGroups[0].renderingElements.isNotEmpty())
    }


    @Test
    fun `Switch between note and rest test`() {
        val simpleNoteSequence = SimpleNoteSequence(listOf(
                NoteSequenceElement.NoteElement(NoteType.A, 5, Duration.QUARTER),
                NoteSequenceElement.NoteElement(NoteType.A, 6, Duration.HALF),
                NoteSequenceElement.NoteElement(NoteType.G, 5, Duration.QUARTER),
                NoteSequenceElement.RestElement(Duration.HALF),
                NoteSequenceElement.NoteElement(NoteType.D, 6, Duration.QUARTER)))

        val sequenceGenerator = SequenceGenerator()
        sequenceGenerator.loadSimpleNoteSequence(simpleNoteSequence)
        val gNote = sequenceGenerator.scoreHandler.scoreData.noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .first { it.note == NoteType.G }

        assertTrue(sequenceGenerator.scoreHandler.scoreData.noteElements.indexOf(gNote) == 2)
        assertTrue(sequenceGenerator.pitchSequence.size == 4)

        val numberOfNoteElments = sequenceGenerator.scoreHandler.scoreData.noteElements.size
        val restElementId = sequenceGenerator.switchBetweenNoteAndRest(gNote.id, -1)
        val pitch = Utilities.getPitch(gNote.note, gNote.octave)
        val foundPitch = sequenceGenerator.pitchSequence.find { it.pitch == pitch }

        assertTrue(foundPitch == null)
        assertTrue(sequenceGenerator.pitchSequence.size == 3)
        assertTrue(sequenceGenerator.scoreHandler.scoreData.noteElements.size == numberOfNoteElments)

        val newNoteElementId = sequenceGenerator.switchBetweenNoteAndRest(restElementId, -1)
        val aNoteSecond = sequenceGenerator.scoreHandler.scoreData.noteElements.filter { it is NoteElement }
                .map { it as NoteElement }
                .find { it.note == NoteType.A && it.octave == 6 }
        val indexOfSecondANote = sequenceGenerator.scoreHandler.scoreData.noteElements.indexOf(aNoteSecond!!)

        assertTrue(sequenceGenerator.scoreHandler.scoreData.noteElements[indexOfSecondANote + 1].id == newNoteElementId)
        assertTrue(sequenceGenerator.pitchSequence.size == 4)
        assertTrue(sequenceGenerator.scoreHandler.scoreData.noteElements.size == numberOfNoteElments)
    }

}