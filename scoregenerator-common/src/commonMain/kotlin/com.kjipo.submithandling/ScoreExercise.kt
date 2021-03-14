package com.kjipo.submithandling

import com.kjipo.score.NoteSequenceElement

class ScoreExercise(val target: List<NoteSequenceElement>) {

    private val attempts = mutableListOf<List<NoteSequenceElement>>()


    fun submit(input: List<NoteSequenceElement>) {
        attempts.add(input)
    }


}