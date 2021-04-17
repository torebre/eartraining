package com.kjipo.handler

import com.kjipo.score.Duration


sealed class ScoreOperation

class InsertNote(val id: String? = null, val pitch: Int, val duration: Duration) : ScoreOperation()

class InsertRest(val id: String? = null, val duration: Duration) : ScoreOperation()

class MoveElement(val id: String, val up: Boolean): ScoreOperation()

class UpdateElement(val id: String, val pitch: Int? = null, val duration: Duration? = null): ScoreOperation()

class DeleteElement(val id: String) : ScoreOperation()
