package com.kjipo.handler

import com.kjipo.score.Accidental
import com.kjipo.score.Duration
import com.kjipo.score.NoteType


interface ScoreHandlerInterface {

    fun getScoreAsJson(): String

    fun moveNoteOneStep(id: String, up: Boolean)

    fun getIdOfFirstSelectableElement(): String?

    fun getNeighbouringElement(activeElement: String, lookLeft: Boolean): String?

    fun updateDuration(id: String, keyPressed: Int)

    fun insertNote(activeElement: String, keyPressed: Int): String?

    fun insertNote(keyPressed: Int): String?

    fun insertNote(activeElement: String, duration: Duration, pitch: Int): String?

    fun insertRest(activeElement: String, duration: Duration): String?

    fun switchBetweenNoteAndRest(idOfElementToReplace: String, keyPressed: Int): String

    fun deleteElement(id: String)

    fun toggleExtra(id: String, extra: Accidental)

    // Add duration on note level
    fun addNoteGroup(duration: Duration, pitches: List<GroupNote>): String?

//    fun getClientContext(): ClientContext

    fun getHighlightElementsMap(): Map<String, Collection<String>>

    class GroupNote(val noteType: NoteType, val octave: Int, val accidental: Accidental? = null)

}