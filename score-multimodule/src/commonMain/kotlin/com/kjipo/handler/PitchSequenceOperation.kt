package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteType


sealed class PitchSequenceOperation(open val id: String?)

class InsertNote(idOfNoteToInsertAfter: String? = null, val pitch: Int, val duration: Duration) : PitchSequenceOperation(idOfNoteToInsertAfter)

class InsertNoteWithType(id: String? = null, val noteType: NoteType, val octave: Int, val duration: Duration) :
    PitchSequenceOperation(id)

class InsertRest(id: String? = null, val duration: Duration) : PitchSequenceOperation(id)

class MoveElement(override val id: String, val up: Boolean) : PitchSequenceOperation(id)

class UpdateElement(override val id: String, val pitch: Int? = null, val duration: Duration? = null) : PitchSequenceOperation(id)

class DeleteElement(override val id: String) : PitchSequenceOperation(id)
