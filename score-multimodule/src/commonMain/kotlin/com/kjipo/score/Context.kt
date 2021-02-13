package com.kjipo.score

import com.kjipo.handler.NoteSymbol

class Context {

    private var idCounter = 0

    fun isAccidental(pitch: Int) {
        // TODO


    }


    // TODO Make proper computation
    fun stemUp(noteId: String) = Stem.UP

    // TODO This only works for C scale and G clef
    fun stemUp(pitch: Int) = if (pitch >= 71) {
        Stem.DOWN
    } else {
        Stem.UP
    }

    fun requiresStem(noteElement: NoteElement): Boolean {
        noteElement.duration.let {
            // TODO Make proper computation
            return it == Duration.HALF || it == Duration.QUARTER || it == Duration.EIGHT
        }
    }

    private fun requiresStem(note: NoteSymbol): Boolean {
        // TODO Make proper computation
        return note.duration == Duration.HALF || note.duration == Duration.QUARTER || note.duration == Duration.EIGHT
    }

    fun requiresStem(noteGroupElement: NoteGroupElement): Boolean {
        return noteGroupElement.notes.map { requiresStem(it) }.filter { it }.any()
    }

    fun getAndIncrementIdCounter() = "context-${idCounter++}"

    fun getClientContext(): ClientContext {
        // TODO
        return ClientContext(emptyMap())
    }


//    private fun addAccidentalIfNeeded(note: NoteType): PositionedRenderingElement? {
//        if (noteRequiresSharp(note)) {
//            return setupAccidental(Accidental.SHARP)
//        }
//        return null
//    }

    fun noteRequiresSharp(note: NoteType): Boolean {
        return when (note) {
            NoteType.A_SHARP, NoteType.C_SHARP, NoteType.D_SHARP, NoteType.F_SHARP, NoteType.G_SHARP -> true
            else -> false
        }
    }

}