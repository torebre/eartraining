package com.kjipo.handler

import com.kjipo.score.Accidental
import com.kjipo.score.NoteType

class GroupNote(val noteType: NoteType, val octave: Int, val accidental: Accidental? = null)
