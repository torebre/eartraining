package com.kjipo.scoregenerator

import com.kjipo.handler.*
import com.kjipo.score.*
import mu.KotlinLogging


/**
 * Stores a sequence of pitches, and wraps a score handler that can create a score based on the pitch sequence.
 */
class ReducedScore : ReducedScoreInterface {
//    private val pitchSequence = mutableListOf<Pitch>()
    private val actionSequence = mutableListOf<Action>()
    private val noteSequence = mutableListOf<NoteSequenceElement>()

    private var scoreHandler = ScoreHandlerWithReducedLogic(Score())
    private var highlightMap: Map<String, Collection<String>> = emptyMap()

    private var currentScore: Score? = null
    private var latestScoreId = 0
    private var currentRenderingSequence =
        RenderingSequenceWithMetaData(RenderingSequence(emptyList(), null, emptyMap()), emptyMap())
    private var changeSet: Map<String, PositionedRenderingElementParent>? = null

    private var idCounter = 0

    var isDirty = true

    private val operationsSinceLastRender = mutableListOf<PitchSequenceOperation>()

    private val logger = KotlinLogging.logger {}

    fun loadSimpleNoteSequence(simpleNoteSequence: SimpleNoteSequence) {
        noteSequence.clear()
        noteSequence.addAll(simpleNoteSequence.elements)

        logger.debug { "Number of elements in sequence ${simpleNoteSequence.elements.size}" }

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

    /**
     * If null is returned then everything should be rerendered
     */
    fun getElementReplacements(): Collection<String>? {
        return currentScore?.let {
            val idsOfElementsToUpdate = mutableListOf<String>()

            logger.debug { "Seeing which elements have updated since last render" }

            for (pitchSequenceOperation in operationsSinceLastRender) {
                var notHandledOperation = false

                logger.debug { "Pitch sequence update operation: $pitchSequenceOperation" }

                idsOfElementsToUpdate.addAll(when (pitchSequenceOperation) {
                    is MoveElement -> {
                        setOf(pitchSequenceOperation.id)
                    }

                    is UpdateElement -> {
                        setOf(pitchSequenceOperation.id)
                    }

                    // TODO Handle other operations

                    else -> {
                        logger.info { pitchSequenceOperation }

                        notHandledOperation = true
                        emptySet()
                    }

                })

                if (notHandledOperation) {
                    return null
                }

            }

            return idsOfElementsToUpdate
        }

    }

    private fun generateChangeSet(): Map<String, PositionedRenderingElementParent>? {
        return getElementReplacements()?.let { elementIdsChanged ->
            val elementsToUpdate = mutableMapOf<String, PositionedRenderingElementParent>()

            val scoreSetup = scoreHandler.getScoreSetup()
            scoreSetup.scoreRenderingElements.forEach { scoreRenderingElement ->
                if (scoreRenderingElement is ElementWithProperties && elementIdsChanged.contains(
                        scoreRenderingElement.getProperty(ELEMENT_ID)
                    )
                ) {
                    scoreRenderingElement.toRenderingElement().forEach { positionedRenderingElementParent ->
                        elementsToUpdate[positionedRenderingElementParent.id] = positionedRenderingElementParent
                    }
                }
            }
            logger.debug { "Calculating changeset. Elements to update: $elementsToUpdate" }

            elementsToUpdate
        }
    }

    private fun updateIfDirty() {
        if (isDirty) {
            currentScore = ScoreElementsTranslator.createRenderingData(noteSequence).also { score ->
                // TODO If the score does not have to be rebuilt the whole time it will be more efficient
                scoreHandler = ScoreHandlerWithReducedLogic(score)

                val (newPitchSequence, newActionSequence) = computePitchSequence(noteSequence)

//                pitchSequence.clear()
//                pitchSequence.addAll(newPitchSequence)

                actionSequence.clear()
                actionSequence.addAll(newActionSequence)

                highlightMap = generateHighlightMap()

                currentRenderingSequence = scoreHandler.build()
            }

            changeSet = generateChangeSet()
            operationsSinceLastRender.clear()
            ++latestScoreId
            isDirty = false
        }
    }


    private fun generateHighlightMap(): MutableMap<String, Collection<String>> {
        val elementToScoreHighlightMap = mutableMapOf<String, Collection<String>>()
        val highlightableElements = scoreHandler.getHighlightableElements()

        for (noteSequenceElement in noteSequence) {
            noteSequenceElement.properties[ELEMENT_ID]?.let { scoreElementId ->
                elementToScoreHighlightMap[scoreElementId] = highlightableElements.filter {
                    it.getProperty(ELEMENT_ID) == scoreElementId
                }
                    .flatMap { it.getIdsOfHighlightElements() }
            }
        }

        return elementToScoreHighlightMap
    }




    fun getActionSequenceScript(): ActionScript {
        updateIfDirty()
        return actionScript(actionSequence)
    }


    override fun moveNoteOneStep(id: String, up: Boolean) {

        // TODO Check that note does not move outside allowed limits
        logger.debug { "Moving note with ID: $id" }

        noteSequence.find { it.id == id }?.let { existingNoteSequenceElement ->
            when (existingNoteSequenceElement) {
                is NoteSequenceElement.NoteElement -> {
                    applyOperation(
                        UpdateElement(
                            id, ScoreHandlerUtilities.getPitch(
                                existingNoteSequenceElement.note,
                                existingNoteSequenceElement.octave
                            ) + if (up) 1 else -1
                        )
                    )
                }

                else -> {
                    // TODO Need to handle notes inside note group
                }

            }
        }
        isDirty = true
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


    override fun applyOperation(operation: PitchSequenceOperation) {
        logger.debug { "Applying operation: $operation" }

        operationsSinceLastRender.add(operation)

        when (operation) {
            is InsertNote -> {
                handleInsertNote(operation)
            }

            is MoveElement -> {
                moveNoteOneStep(operation.id, operation.up)
            }

            is DeleteElement -> {
                deleteElement(operation.id)
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

        updateIfDirty()
    }

    override fun getLatestId() = latestScoreId

    override fun getChangeSet(scoreId: Int): RenderingSequenceUpdate? {
        return if (scoreId == latestScoreId - 1) {
            changeSet?.let {
                RenderingSequenceUpdate(it, null)
            }
        } else {
            null
        }
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
        noteSequence.find { it.id == operation.id }?.let { existingElement ->
            when (existingElement) {
                is NoteSequenceElement.NoteElement -> {
                    updateElement(operation.pitch, operation.duration, existingElement)
                }

                else -> {
                    // TODO Need to handle notes inside note group
                }

            }
        }
    }


    private fun updateElement(pitch: Int?, duration: Duration?, existingElement: NoteSequenceElement.NoteElement) {
        val (noteType, octave) = pitch?.let {
            ScoreHandlerUtilities.pitchToNoteAndOctave(it)
        } ?: Pair(existingElement.note, existingElement.octave)
        val durationAfterUpdate = duration ?: existingElement.duration

        replaceElement(
            NoteSequenceElement.NoteElement(
                existingElement.id,
                noteType,
                octave,
                durationAfterUpdate,
                mapOf(Pair(ELEMENT_ID, existingElement.id))
            ), existingElement
        )

        isDirty = true
    }

    private fun handleInsertNote(insertNote: InsertNote) {
        val duration = insertNote.duration
        val id = insertNote.id

        if (id != null) {
            insertNote(id, duration, insertNote.pitch)
        } else {
            insertNote(duration, insertNote.pitch)
        }
    }

    fun getCurrentNoteSequence() = noteSequence.toList()

}