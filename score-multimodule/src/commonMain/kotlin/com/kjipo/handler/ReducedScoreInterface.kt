package com.kjipo.handler

import com.kjipo.score.Duration
import com.kjipo.score.RenderingSequenceUpdate

interface ReducedScoreInterface {

    fun getScoreAsJson(): String

    fun moveNoteOneStep(id: String, up: Boolean)

    fun getIdOfFirstSelectableElement(): String?

    fun getNeighbouringElement(activeElement: String?, lookLeft: Boolean): String?

    fun deleteElement(id: String)

    // Add duration on note level
    fun addNoteGroup(duration: Duration, pitches: List<GroupNote>): String?

    fun getHighlightElementsMap(): Map<String, Collection<String>>

    fun applyOperation(operation: PitchSequenceOperation)

    fun getLatestId(): Int
    fun getChangeSet(scoreId: Int): RenderingSequenceUpdate?
}