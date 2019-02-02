package com.kjipo.scoregenerator


import junit.framework.TestCase.*
import org.hamcrest.core.IsNot.not
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

        println(renderingSequence)


    }


}