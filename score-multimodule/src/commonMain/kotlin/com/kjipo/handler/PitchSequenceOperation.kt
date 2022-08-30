package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType


sealed class PitchSequenceOperation

class InsertNote(val id: String? = null, val pitch: Int, val duration: Duration) : PitchSequenceOperation()

class InsertNoteWithType(val id: String? = null, val noteType: NoteType, val octave: Int, val duration: Duration) :
    PitchSequenceOperation()

class InsertRest(val id: String? = null, val duration: Duration) : PitchSequenceOperation()

class MoveElement(val id: String, val up: Boolean) : PitchSequenceOperation()

class UpdateElement(val id: String, val pitch: Int? = null, val duration: Duration? = null) : PitchSequenceOperation()

class DeleteElement(val id: String) : PitchSequenceOperation()
