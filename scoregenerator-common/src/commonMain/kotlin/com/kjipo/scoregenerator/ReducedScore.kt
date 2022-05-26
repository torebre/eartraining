package com.kjipo.scoregenerator

import com.kjipo.handler.*
import com.kjipo.score.Duration
import com.kjipo.score.HighlightableElement
import com.kjipo.score.NoteSequenceElement
import com.kjipo.score.NoteType
import mu.KotlinLogging


/**
 * Stores a sequence of pitches, and wraps a score handler that can create a score based on the pitch sequence.
 */
class ReducedScore : ReducedScoreInterface {
    private val pitchSequence = mutableListOf<Pitch>()
    private val actionSequence = mutableListOf<Action>()
    val noteSequence = mutableListOf<NoteSequenceElement>()

    private var scoreHandler = ScoreHandlerWithReducedLogic(Score())
    private var highlightMap: Map<String, Collection<String>> = emptyMap()

    var idCounter = 0

    var isDirty = true

    private val logger = KotlinLogging.logger {}

    fun loadSimpleNoteSequence(simpleNoteSequence: SimpleNoteSequence) {
        noteSequence.clear()
        noteSequence.addAll(simpleNoteSequence.elements)

        logger.debug { "Number of elements in sequence ${simpleNoteSequence.elements.size}" }

        isDirty = true
    }

    override fun updateDuration(id: String, duration: Duration) {
        val element = noteSequence.find { id == it.id } ?: return
        val elementId = (++idCounter).toString()

        when (element) {
            is NoteSequenceElement.NoteElement -> {
                replaceElement(
                    NoteSequenceElement.NoteElement(
                        elementId,
                        element.note,
                        element.octave,
                        duration,
                        mapOf(Pair(ELEMENT_ID, elementId))
                    ), element
                )
            }
            is NoteSequenceElement.RestElement -> {
                replaceElement(
                    NoteSequenceElement.RestElement(
                        (++idCounter).toString(),
                        duration,
                        mapOf(Pair(ELEMENT_ID, elementId))
                    ), element
                )
            }

            is NoteSequenceElement.MultipleNotesElement -> {
                TODO("Not implemented")
            }


        }

        isDirty = true
    }

    override fun getScoreAsJson(): String {
        updateIfDirty()
        return scoreHandler.getScoreAsJson()
    }

    override fun getHighlightElementsMap(): Map<String, Collection<String>> {
        updateIfDirty()
        return highlightMap
    }

    internal fun getScore(): Score {
        updateIfDirty()
        return scoreHandler.score
    }

    private fun updateIfDirty() {
        if (isDirty) {

            val score = ScoreElementsTranslator.createRenderingData(noteSequence)

            // TODO If the score does not have to be rebuilt the whole time it will be more efficient
            scoreHandler = ScoreHandlerWithReducedLogic(score)

            val (newPitchSequence, newActionSequence) = computePitchSequence()

            pitchSequence.clear()
            pitchSequence.addAll(newPitchSequence)

            actionSequence.clear()
            actionSequence.addAll(newActionSequence)

            highlightMap = generateHighlightMap()

            isDirty = false
        }
    }


    private fun generateHighlightMap(): MutableMap<String, Collection<String>> {
        val elementToScoreHighlightMap = mutableMapOf<String, Collection<String>>()
        val highlightableElements = scoreHandler.getHighlightableElements()

        for (noteSequenceElement in noteSequence) {
            noteSequenceElement.properties[ELEMENT_ID]?.let { scoreElementId ->
                elementToScoreHighlightMap[scoreElementId] = highlightableElements.filter {
                    it.properties[ELEMENT_ID] == scoreElementId
                }
                    .flatMap { it.getIdsOfHighlightElements() }
            }
        }

        return elementToScoreHighlightMap
    }

    private fun computePitchSequence(): Pair<MutableList<Pitch>, MutableList<Action>> {
        var timeCounter = 0
        val newPitchSequence = mutableListOf<Pitch>()
        val newActionSequence = mutableListOf<Action>()

        for (noteSequenceElement in noteSequence) {
            when (noteSequenceElement) {
                is NoteSequenceElement.NoteElement -> {
                    val durationInMilliseconds =
                        ScoreHandlerUtilities.getDurationInMilliseconds(noteSequenceElement.duration)
                    handleNoteOrRest(
                        timeCounter,
                        durationInMilliseconds,
                        true,
                        noteSequenceElement.note,
                        noteSequenceElement.octave,
                        noteSequenceElement.id,
                        noteSequenceElement.duration,
                        newActionSequence
                    )

                    timeCounter += durationInMilliseconds
                }
                is NoteSequenceElement.RestElement -> {
                    val durationInMilliseconds =
                        ScoreHandlerUtilities.getDurationInMilliseconds(noteSequenceElement.duration)
                    handleNoteOrRest(
                        timeCounter,
                        durationInMilliseconds,
                        false,
                        NoteType.C,
                        5,
                        noteSequenceElement.id,
                        noteSequenceElement.duration,
                        newActionSequence
                    )
                    timeCounter += durationInMilliseconds
                }
                is NoteSequenceElement.MultipleNotesElement -> {
                    timeCounter = handleMultipleNotesElement(
                        timeCounter,
                        noteSequenceElement,
                        newActionSequence,
                        newPitchSequence
                    )
                }
            }
        }

        return Pair(newPitchSequence, newActionSequence)
    }

    private fun handleNoteOrRest(
        timeCounter: Int,
        durationInMilliseconds: Int,
        isNote: Boolean,
        noteType: NoteType,
        octave: Int,
        id: String,
        duration: Duration,
        actionSequence: MutableList<Action>
    ) {
        val pitchOn = timeCounter
        val pitchOff = timeCounter + durationInMilliseconds

        if (isNote) {
            val pitch =
                ScoreHandlerUtilities.getPitch(noteType, octave)

            pitchSequence.add(
                Pitch(
                    id,
                    pitchOn,
                    pitchOff,
                    pitch,
                    duration
                )
            )

            with(actionSequence) {
                add(Action.PitchEvent(pitchOn, listOf(pitch), true))
                add(Action.PitchEvent(pitchOff, listOf(pitch), false))
                add(Action.HighlightEvent(pitchOn, true, setOf(id)))
                add(Action.HighlightEvent(pitchOff, false, setOf(id)))
            }
        } else {
            // This is a rest
            with(actionSequence) {
                add(Action.HighlightEvent(pitchOn, true, setOf(id)))
                add(Action.HighlightEvent(pitchOff, false, setOf(id)))
            }
        }

        actionSequence.sortBy { it.time }
    }

    private fun handleMultipleNotesElement(
        timeCounter: Int,
        noteGroup: NoteSequenceElement.MultipleNotesElement,
        newActionSequence: MutableList<Action>,
        newPitchSequence: MutableList<Pitch>
    ): Int {
        val duration = noteGroup.elements.first().duration
        val updatedTimeCounter = timeCounter + ScoreHandlerUtilities.getDurationInMilliseconds(duration)

        val pitches = mutableListOf<Int>()
        for (event in noteGroup.elements) {
            if (duration != event.duration) {
                throw UnsupportedOperationException("Only durations of same length handled for multiple notes elements so far")
            }

            val pitch = ScoreHandlerUtilities.getPitch(event.note, event.octave)
            pitches.add(pitch)

            newPitchSequence.add(
                Pitch(
                    event.id,
                    timeCounter,
                    updatedTimeCounter,
                    pitch,
                    duration
                )
            )
        }

        with(newActionSequence) {
            add(Action.PitchEvent(timeCounter, pitches, true))
            add(Action.PitchEvent(updatedTimeCounter, pitches, false))
            add(Action.HighlightEvent(timeCounter, true, setOf(noteGroup.id)))
            add(Action.HighlightEvent(updatedTimeCounter, false, setOf(noteGroup.id)))
        }


        return updatedTimeCounter
    }

    fun getActionSequenceScript(): ActionScript {
        updateIfDirty()

        val timeEventMap = mutableMapOf<Int, MutableList<Action>>()
        val eventTimes = mutableSetOf<Int>()

        logger.debug { "Action sequence pitch length: ${actionSequence.filter { it is Action.PitchEvent }.count()}" }

        actionSequence.forEach {
            val eventsAtTime = timeEventMap.getOrPut(it.time, { mutableListOf() })
            eventsAtTime.add(it)
            eventTimes.add(it.time)
        }

        var previousEventTime = 0
        return ActionScript(eventTimes.distinct().sorted().mapNotNull {
            val sleepTime = it - previousEventTime

            val result = Pair(sleepTime, timeEventMap[it]!!)
            previousEventTime = it

            result
        }.toList())
    }

    override fun moveNoteOneStep(id: String, up: Boolean) {

        logger.debug { "Moving note with ID: $id" }

        noteSequence.find { it.id == id }?.let {
            when (it) {
                is NoteSequenceElement.NoteElement -> {
                    handleMoveNoteForNote(it, up)

                }
                // TODO Need to handle notes inside note group

            }
        }
        isDirty = true
    }

    private fun handleMoveNoteForNote(noteElement: NoteSequenceElement.NoteElement, up: Boolean) {
        if (up) {
            moveNoteUp(noteElement)
        } else {
            moveNoteDown(noteElement)
        }
    }

    private fun moveNoteDown(noteElement: NoteSequenceElement.NoteElement) {
        // The ID of the new element is set to the same as the old
        if (noteElement.note == NoteType.C) {
            replaceElement(
                NoteSequenceElement.NoteElement(
                    noteElement.id,
                    NoteType.H, noteElement.octave - 1, noteElement.duration,
                    mapOf(Pair(ELEMENT_ID, noteElement.id))
                ), noteElement
            )
        } else {
            val noteTypeValue = (noteElement.note.ordinal - 1).let {
                if (it < 0) {
                    it + NoteType.values().size
                } else {
                    it
                }
            }
            replaceElement(
                NoteSequenceElement.NoteElement(
                    noteElement.id,
                    NoteType.values()[noteTypeValue],
                    noteElement.octave,
                    noteElement.duration,
                    mapOf(Pair(ELEMENT_ID, noteElement.id))
                ), noteElement
            )
        }
    }

    private fun moveNoteUp(noteElement: NoteSequenceElement.NoteElement) {
        // The ID of the new element is set to the same as the old
        if (noteElement.note == NoteType.H) {
            replaceElement(
                NoteSequenceElement.NoteElement(
                    noteElement.id,
                    NoteType.C,
                    noteElement.octave + 1,
                    noteElement.duration,
                    mapOf(Pair(ELEMENT_ID, noteElement.id))
                ), noteElement
            )
        } else {
            replaceElement(
                NoteSequenceElement.NoteElement(
                    noteElement.id,
                    NoteType.values()[(noteElement.note.ordinal + 1) % NoteType.values().size],
                    noteElement.octave,
                    noteElement.duration,
                    mapOf(Pair(ELEMENT_ID, noteElement.id))
                ), noteElement
            )
        }
    }

    private fun replaceElement(
        newNoteSequenceElement: NoteSequenceElement,
        oldNoteSequenceElement: NoteSequenceElement
    ) {
        noteSequence.find { oldNoteSequenceElement.id == it.id }?.let {
            val index = noteSequence.indexOf(it)

            logger.debug { "Element to replace: $index" }

            noteSequence.add(index, newNoteSequenceElement)
            noteSequence.removeAt(index + 1)
        }
    }

    override fun getIdOfFirstSelectableElement() = noteSequence.firstOrNull()?.id

    override fun getNeighbouringElement(activeElement: String?, lookLeft: Boolean): String? {
        if (activeElement == null) {
            return if (lookLeft) {
                getIdOfFirstSelectableElement()
            } else {
                noteSequence.lastOrNull()?.id
            }
        } else {
            noteSequence.find { it.id == activeElement }?.let {
                return getNeighbouringElementInternal(it, lookLeft)
            }
        }
        return null
    }

    private fun getNeighbouringElementInternal(activeElement: NoteSequenceElement, lookLeft: Boolean): String {
        return noteSequence.indexOf(activeElement).let {
            if (lookLeft) {
                if (it == 0) {
                    noteSequence.last().id
                } else {
                    noteSequence[it - 1].id
                }
            } else {
                if (it == noteSequence.size - 1) {
                    noteSequence.first().id
                } else {
                    noteSequence[it + 1].id
                }
            }
        }
    }

    private fun insertNote(duration: Duration, pitch: Int): String {
        val (note, octave) = ScoreHandlerUtilities.pitchToNoteAndOctave(pitch)
        val elementId = "note_${++idCounter}"
        noteSequence.add(
            NoteSequenceElement.NoteElement(
                elementId,
                note,
                octave,
                duration,
                mapOf(Pair(ELEMENT_ID, elementId))
            )
        )
        isDirty = true
        return noteSequence.last().id
    }

    override fun deleteElement(id: String) {
        noteSequence.find { it.id == id }?.let {
            noteSequence.remove(it)
            isDirty = true
        }
    }

    private fun insertNote(activeElement: String, duration: Duration, pitch: Int = 60): String? {
        noteSequence.find { it.id == activeElement }?.let { element ->

            // TODO Only hardcoded note type and octave for testing

            val insertIndex = noteSequence.indexOf(element) + 1
            val elementId = "note_${++idCounter}"

            val (note, octave) = ScoreHandlerUtilities.pitchToNoteAndOctave(pitch)
            noteSequence.add(
                insertIndex,
                NoteSequenceElement.NoteElement(elementId, note, octave, duration, mapOf(Pair(ELEMENT_ID, elementId)))
            )
            isDirty = true
            return noteSequence[insertIndex].id
        }
        return null
    }

    override fun addNoteGroup(duration: Duration, pitches: List<GroupNote>): String {
        val groupNotes = pitches.map {
            val elementId = "note_${++idCounter}"
            NoteSequenceElement.NoteElement(
                elementId,
                it.noteType,
                it.octave,
                duration,
                mapOf(Pair(ELEMENT_ID, elementId))
            )
        }.toList()
        val elementId = "notegroup_${++idCounter}"
        noteSequence.add(
            NoteSequenceElement.MultipleNotesElement(
                elementId,
                groupNotes,
                duration,
                mapOf(Pair(ELEMENT_ID, elementId))
            )
        )
        isDirty = true
        return noteSequence.last().id
    }

    private fun insertRest(activeElement: String?, duration: Duration): String {
        val elementId = "rest_${++idCounter}"

        return if (activeElement == null) {
            with(noteSequence) {
                add(NoteSequenceElement.RestElement(elementId, duration, mapOf(Pair(ELEMENT_ID, elementId))))
                isDirty = true
                last().id
            }
        } else {
            noteSequence.find { it.id == activeElement }?.let { element ->
                val insertIndex = noteSequence.indexOf(element) + 1
                noteSequence.add(
                    insertIndex,
                    NoteSequenceElement.RestElement(elementId, duration, mapOf(Pair(ELEMENT_ID, elementId)))
                )
                isDirty = true
                return noteSequence[insertIndex].id
            }
            return noteSequence.last().id
        }
    }


    override fun applyOperation(operation: ScoreOperation): String? {
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
            is InsertRest -> {
                handleInsertRest(operation)
            }
            is InsertNoteWithType -> {
                handleInsertNoteWithType(operation)
            }
        }

        // TODO
        return null
    }

    private fun handleInsertNoteWithType(operation: InsertNoteWithType): String {
        val elementId = "note_${++idCounter}"
        noteSequence.add(
            NoteSequenceElement.NoteElement(
                elementId,
                operation.noteType,
                operation.octave,
                operation.duration,
                mapOf(Pair(ELEMENT_ID, elementId))
            )
        )
        isDirty = true
        return noteSequence.last().id
    }

    private fun handleInsertRest(operation: InsertRest) {
        insertRest(operation.id, operation.duration)
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
        val duration = insertNote.duration
        val id = insertNote.id

        if (id != null) {
            insertNote(id, duration, insertNote.pitch)
        } else {
            insertNote(duration, insertNote.pitch)
        }
    }

}