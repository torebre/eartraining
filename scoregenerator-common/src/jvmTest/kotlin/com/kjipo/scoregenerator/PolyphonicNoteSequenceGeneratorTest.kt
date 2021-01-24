package com.kjipo.scoregenerator

import kotlin.test.Test

class PolyphonicNoteSequenceGeneratorTest {


    @Test
    fun generateSequenceTest() {
        val polyphonicNoteSequenceGenerator = PolyphonicNoteSequenceGenerator()
        val noteSequence = polyphonicNoteSequenceGenerator.createSequence()

        println("Note sequence:")
        for (noteEvent in noteSequence.elements) {
            println("Element: $noteEvent")
        }

        val transformedSequence = PolyphonicNoteSequenceGenerator.transformToSimplePitchEventSequence(noteSequence)

        for (pitchEvent in transformedSequence.pitches) {
            println("Pitch event: $pitchEvent")
        }
    }


}