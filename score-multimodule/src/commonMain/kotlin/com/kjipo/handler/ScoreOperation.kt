package com.kjipo.handler

import com.kjipo.score.Duration


sealed class ScoreOperation {

    class InsertNote(id: String?, val duration: Duration)

    class DeleteElement(id: String)

}