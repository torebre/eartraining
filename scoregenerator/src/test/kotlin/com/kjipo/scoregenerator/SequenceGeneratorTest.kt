package com.kjipo.scoregenerator


import junit.framework.TestCase.assertEquals
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


}