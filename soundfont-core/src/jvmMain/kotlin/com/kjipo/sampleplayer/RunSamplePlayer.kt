package com.kjipo.sampleplayer

import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.SetupData
import com.kjipo.soundfontparser.loadFile
import com.kjipo.soundfontparser.printChunks
import kotlinx.coroutines.runBlocking


fun main() {
    val soundFontData = runBlocking {
        val soundfontData = loadFile("florestan-subset.sf2")
        val chunks = Parser.parse(soundfontData)
        printChunks(chunks)
        SetupData.setupData(chunks, soundfontData)
    }

    val samplePlayer = SamplePlayer(soundFontData)
    samplePlayer.playSample(1)

}



