package com.kjipo.viewer

import com.kjipo.svg.RenderingSequence
import tornadofx.*


class ScoreController : Controller() {
    private val listeners = mutableListOf<ScoreControllerListener>()

    fun fireLoadScore(renderingSequence: RenderingSequence) = listeners.forEach({ it.load(renderingSequence) })

    fun fireNoteOn(noteId: Int) = listeners.forEach({ it.noteOn(noteId) })

    fun fireNoteOff(noteId: Int) = listeners.forEach({ it.noteOff(noteId) })

    fun addListener(listener: ScoreControllerListener) = listeners.add(listener)

}


interface ScoreControllerListener {

    fun load(renderingSequence: RenderingSequence)

    fun noteOn(noteId: Int)

    fun noteOff(noteId: Int)

}