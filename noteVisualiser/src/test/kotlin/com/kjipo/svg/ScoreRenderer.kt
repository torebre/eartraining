package com.kjipo.svg


interface ScoreRenderer {

    fun load(renderingSequence: RenderingSequence)

    fun noteOn(noteId: Int)

    fun noteOff(noteId: Int)

}