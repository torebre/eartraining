package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType


data class ScoreHandlerElement(val id: String, var duration: Duration, var isNote: Boolean, var octave: Int, var noteType: NoteType)