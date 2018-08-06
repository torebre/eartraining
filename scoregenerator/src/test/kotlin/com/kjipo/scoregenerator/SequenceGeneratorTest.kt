package com.kjipo.scoregenerator

import org.junit.Test

class SequenceGeneratorTest {


    @Test
    fun scoregeneratorTest() {
        val sequenceGenerator = SequenceGenerator()
        val sequence = sequenceGenerator.createNewSequence()

        println(sequence)


    }


}