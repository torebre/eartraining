package com.kjipo.handler

import com.kjipo.score.*

object ScoreHelperFunctions {

    fun transformToNoteAndAccidental(noteType: NoteType): Pair<GClefNoteLine, Accidental?> {
        return when (noteType) {
            NoteType.A -> Pair(GClefNoteLine.A, null)
            NoteType.A_SHARP -> Pair(GClefNoteLine.A, Accidental.SHARP)
            NoteType.H -> Pair(GClefNoteLine.H, null)
            NoteType.C -> Pair(GClefNoteLine.C, null)
            NoteType.C_SHARP -> Pair(GClefNoteLine.C, Accidental.SHARP)
            NoteType.D -> Pair(GClefNoteLine.D, null)
            NoteType.D_SHARP -> Pair(GClefNoteLine.D, Accidental.SHARP)
            NoteType.E -> Pair(GClefNoteLine.E, null)
            NoteType.F -> Pair(GClefNoteLine.F, null)
            NoteType.F_SHARP -> Pair(GClefNoteLine.F, Accidental.SHARP)
            NoteType.G -> Pair(GClefNoteLine.G, null)
            NoteType.G_SHARP -> Pair(GClefNoteLine.G, Accidental.SHARP)
        }

    }

    internal fun createTemporalElement(element: ScoreHandlerElement, context: Context): ScoreRenderingElement {
        return when (element) {
            is Note -> {
                NoteElement(
                    element,
                    context,
                ).also {
                    element.properties.forEach { entry -> it.setProperty(entry.key, entry.value) }
                }
            }

            is Rest -> {
                RestElement(
                    element.duration,
                    context,
                    id = element.id,
                ).also {
                    element.properties.forEach { entry -> it.setProperty(entry.key, entry.value) }
                }
            }

            is NoteGroup -> {
                // TODO Handle duration on note level
                return NoteGroupElement(
                    element,
                    context,
                ).also {
                    element.properties.forEach { entry -> it.setProperty(entry.key, entry.value) }
                }
            }

        }

    }
}