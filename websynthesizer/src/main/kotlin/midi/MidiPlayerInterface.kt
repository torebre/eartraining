package com.kjipo.midi

interface MidiPlayerInterface {

    fun noteOn(pitch: Int)

    fun noteOff(pitch: Int)

    fun releaseAll()

    fun start()

    fun stop()

}
