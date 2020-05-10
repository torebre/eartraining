package com.kjipo.handler

import com.kjipo.score.Duration


sealed class ScoreOperation

class InsertNote(val id: String?, val duration: Duration) : ScoreOperation()

class DeleteElement(val id: String) : ScoreOperation()
