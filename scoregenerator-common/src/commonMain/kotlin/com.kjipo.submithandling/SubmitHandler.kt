package com.kjipo.submithandling

import com.kjipo.score.NoteSequenceElement


class SubmitHandler {
    private val exercises = mutableListOf<ScoreExercise>()


    fun setupExercise(target: List<NoteSequenceElement>): ScoreExercise {
        val exercise = ScoreExercise(target)
        exercises.add(exercise)
        return exercise
    }

    fun getCurrentExercise() = exercises.lastOrNull()


}