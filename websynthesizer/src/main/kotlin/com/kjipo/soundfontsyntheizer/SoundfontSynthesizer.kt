package com.kjipo.soundfontsyntheizer

import com.kjipo.midi.MidiPlayerInterface
import com.kjipo.sampleplayer.SamplePlayerInteractive
import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.SetupData
import com.kjipo.soundfontparser.loadFile
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class SoundfontSynthesizer: MidiPlayerInterface {
    private lateinit var samplePlayerInteractive: SamplePlayerInteractive

    private val logger = KotlinLogging.logger {}

    init {
        GlobalScope.launch {
            val soundfontData = loadFile("grandpno/Grandpno.sf2")
//            val soundfontData = loadFile("German8.sf2")
            val chunks = Parser.parse(soundfontData)
            val soundFontData = SetupData.setupData(chunks, soundfontData)

            samplePlayerInteractive = SamplePlayerInteractive(soundFontData)
        }

    }

    override fun noteOn(pitch: Int) {
        samplePlayerInteractive.pitchOn(pitch)
    }

    override fun noteOff(pitch: Int) {
        samplePlayerInteractive.pitchOff(pitch)
    }

    override fun releaseAll() {
        samplePlayerInteractive.allPitchesOff()
    }

    override fun start() {
        // Nothing to do here
    }

    override fun stop() {
        // Nothing to do here
    }

}