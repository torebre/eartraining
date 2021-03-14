package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType

class ScoreActionHandler(scoreHandlerElements: MutableList<ScoreHandlerElement>, private val initialId: Int) {

    private var scoreHandlerElements: MutableList<ScoreHandlerElement> = scoreHandlerElements
        set(value) {
            field = value
            idCounter = initialId
        }

    private var idCounter = initialId


    fun clear() {
        scoreHandlerElements = mutableListOf()
    }

    fun moveNoteOneStep(id: String, up: Boolean) {
        scoreHandlerElements.find { it.id == id }?.let {
            when (it) {
                is NoteOrRest -> {
                    handleMoveNoteForNoteOrRest(it, up)

                }
                // TODO Need to handle notes inside note group


            }
        }
    }

    private fun handleMoveNoteForNoteOrRest(noteOrRestElement: NoteOrRest, up: Boolean) {
        if (!noteOrRestElement.isNote) {
            // Cannot move an element which is not a note
            return
        }

        if (up) {
            if (noteOrRestElement.noteType == NoteType.H) {
                noteOrRestElement.noteType = NoteType.C
                ++noteOrRestElement.octave
            } else {
                noteOrRestElement.noteType =
                    NoteType.values()[(noteOrRestElement.noteType.ordinal + 1) % NoteType.values().size]
            }
        } else {
            if (noteOrRestElement.noteType == NoteType.C) {
                noteOrRestElement.noteType = NoteType.H
                --noteOrRestElement.octave
            } else {
                noteOrRestElement.noteType =
                    NoteType.values()[(NoteType.values().size + noteOrRestElement.noteType.ordinal - 1) % NoteType.values().size]
            }
        }
    }

    fun getIdOfFirstSelectableElement() = scoreHandlerElements.firstOrNull()?.id

    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String? {
        return scoreHandlerElements.find { it.id == activeElement }?.let { scoreHandlerElement ->
            val indexOfElement = scoreHandlerElements.indexOf(scoreHandlerElement)
            if (lookLeft) {
                if (indexOfElement == 0) {
                    scoreHandlerElement.id
                } else {
                    scoreHandlerElements[indexOfElement - 1].id
                }
            } else {
                if (indexOfElement == scoreHandlerElements.size - 1) {
                    scoreHandlerElement.id
                } else {
                    scoreHandlerElements[indexOfElement + 1].id
                }
            }
        }
    }

    fun updateDuration(id: String, keyPressed: Int) {
        scoreHandlerElements.find { it.id == id }?.let {
            when (it) {
                is NoteOrRest -> it.duration = ScoreHandlerUtilities.getDuration(keyPressed)

                // TODO Need to handle notes inside note group

            }

        }
    }

    fun insertNote(activeElement: String, keyPressed: Int) =
        insertNote(activeElement, ScoreHandlerUtilities.getDuration(keyPressed))

    fun insertNote(activeElement: String, duration: Duration): String? {
        return scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreHandlerElements.indexOf(element) + 1
            scoreHandlerElements.add(
                insertIndex,
                NoteOrRest((++idCounter).toString(), duration, true, 5, NoteType.C)
            )
            return scoreHandlerElements[insertIndex].id
        }
    }

    fun insertNote(keyPressed: Int) = insertNote(ScoreHandlerUtilities.getDuration(keyPressed))

    fun insertNote(duration: Duration): String {
        scoreHandlerElements.add(NoteOrRest((++idCounter).toString(), duration, true, 5, NoteType.C))
        return scoreHandlerElements.last().id
    }

    fun insertNote(duration: Duration, pitch: Int): String {
        val noteAndOctave = ScoreHandlerUtilities.pitchToNoteAndOctave(pitch)
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                true,
                noteAndOctave.second,
                noteAndOctave.first
            )
        )
        return scoreHandlerElements.last().id
    }

    fun insertNote(duration: Duration, octave: Int, noteType: NoteType): String {
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                true,
                octave,
                noteType
            )
        )
        return scoreHandlerElements.last().id
    }

    fun insertRest(duration: Duration): String {
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                false,
                5,
                NoteType.C
            )
        )
        return scoreHandlerElements.last().id
    }

    fun insertChord(duration: Duration, elements: Collection<NoteSequenceElement.NoteElement>) {
        val noteGroupElement = NoteGroup((++idCounter).toString(), elements.map {
            // TODO Need to handle accidentals here?
            NoteSymbol((++idCounter).toString(), duration, it.octave, it.note)
        }.toList())

        scoreHandlerElements.add(noteGroupElement)
    }


    fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String {
        scoreHandlerElements.find { it.id == idOfElementToReplace }?.let {
            when (it) {
                is NoteOrRest -> it.isNote = !it.isNote
                // TODO Need to handle notes inside note groups
            }

        }
        return idOfElementToReplace
    }

    fun deleteElement(id: String) {
        scoreHandlerElements.find { it.id == id }?.let {
            scoreHandlerElements.remove(it)
        }
    }

    fun findNoteType(id: String): NoteType? {
        return scoreHandlerElements.find { it.id == id }?.let {
            when (it) {
                is NoteOrRest -> {
                    if (it.isNote) {
                        it.noteType
                    } else {
                        null
                    }
                }
                // TODO Need to handle notes inside note group
                else -> null

            }
        }
    }

    fun findScoreHandlerElement(id: String) = scoreHandlerElements.find { it.id == id }

    fun getScoreHandlerElements() = scoreHandlerElements

    fun insertNote(activeElement: String, duration: Duration, pitch: Int = 60): String? {
        scoreHandlerElements.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = scoreHandlerElements.indexOf(element) + 1

            val noteAndOctave = ScoreHandlerUtilities.pitchToNoteAndOctave(pitch)
            scoreHandlerElements.add(
                insertIndex,
                NoteOrRest((++idCounter).toString(), duration, true, noteAndOctave.second, noteAndOctave.first)
            )
            return scoreHandlerElements[insertIndex].id
        }
        return null
    }

    fun addNoteGroup(duration: Duration, pitches: List<ScoreHandlerInterface.GroupNote>): String {
        val groupNotes = pitches.map {
            NoteSymbol((++idCounter).toString(), duration, it.octave, it.noteType)
        }.toList()
        scoreHandlerElements.add(NoteGroup((++idCounter).toString(), groupNotes))
        return scoreHandlerElements.last().id
    }

//     fun getHighlightElementsMap(): Map<String, Collection<String>> {
//        return cachedBuild?.highlightElementsMap ?: build().highlightElementsMap
//    }

    fun insertRest(activeElement: String, duration: Duration): String {
        scoreHandlerElements.add(
            NoteOrRest(
                (++idCounter).toString(),
                duration,
                false,
                5,
                NoteType.C,
            )
        )
        return scoreHandlerElements.last().id
    }


    fun applyOperation(operation: ScoreOperation): String? {
        when (operation) {
            is InsertNote -> {
                handleInsertNote(operation)


            }
            is MoveElement -> {
                handleMoveElement(operation)
            }
            is DeleteElement -> {
                handleDeleteElement(operation)
            }
            is UpdateElement -> {
                handleUpdateElement(operation)
            }

        }

        // TODO
        return null
    }

    private fun handleUpdateElement(operation: UpdateElement) {
       // TODO
    }

    private fun handleDeleteElement(operation: DeleteElement) {
        deleteElement(operation.id)
    }

    private fun handleMoveElement(operation: MoveElement) {
        moveNoteOneStep(operation.id, operation.up)
    }

    private fun handleInsertNote(insertNote: InsertNote) {
        // Default to quarter if nothing is set
        val duration = insertNote.duration ?: Duration.QUARTER
        if (insertNote.id != null) {
            insertNote(insertNote.id, duration)
        }
        insertNote(duration)
    }

}