package com.kjipo.soundfontparser

import com.kjipo.sampleplayer.SamplePlayer
import kotlinx.coroutines.runBlocking


fun main() {

    runBlocking {
//        val soundfontDataRaw = loadFile("florestan-subset.sf2")
//        val soundfontDataRaw = loadFile("R-piano.sf2")
//        val soundfontDataRaw = loadFile("Grandpno.sf2")
        val soundfontDataRaw = loadFile("Velocity Grand Piano.sf2")
        val chunks = Parser.parse(soundfontDataRaw)
//        printChunks(chunks)

        val soundfontData = SetupData.setupData(chunks, soundfontDataRaw)
        val samplePlayer = SamplePlayer(soundfontData)

        for (sampleName in samplePlayer.getSampleNames()) {
            println(sampleName)
        }

        samplePlayer.getSampleHeaderData().forEach {
            println("${it.achSampleName}. Pitch: ${it.byOriginalPitch}")
        }

//        val sample = samplePlayer.createSample(0, 2, 1.0)
//        samplePlayer.playScale(0)

//        val sample = samplePlayer.generatePitchSample(60, 2)
//        val sample2 = samplePlayer.generatePitchSample(90, 2)

//        println("Sample length: ${sample?.sampleData?.size}")
//        println("Sample2 length: ${sample2?.sampleData?.size}")


    }

}



