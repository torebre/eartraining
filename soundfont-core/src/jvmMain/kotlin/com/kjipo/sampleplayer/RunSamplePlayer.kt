package com.kjipo.sampleplayer

import com.kjipo.soundfontparser.InstSubchunk
import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.PhdrSubchunk
import com.kjipo.soundfontparser.SetupData
import com.kjipo.soundfontparser.SoundFontData
import com.kjipo.soundfontparser.loadFile
import com.kjipo.soundfontparser.printChunks
import com.kjipo.soundfontparser.serialization.SoundFontDataJsonSerializer
import kotlinx.coroutines.runBlocking
import java.io.File


private fun filterSoundFont() {
    val soundFontData = runBlocking {
        val soundfontData = loadFile("FluidR3Mono_GM2-315.SF2")
        val chunks = Parser.parse(soundfontData)
//        printChunks(chunks)
        val soundFont = SetupData.setupData(chunks, soundfontData)

//        val pianoInstrument = soundFont.instSubchunk.sfInstRecords.find { sfInstRecord -> sfInstRecord.achInstName == "Yamaha Grand Piano" }
//        if(pianoInstrument == null) {
//            throw IllegalStateException("Yamaha Grand Piano not found")
//        }

        val pianoShdrRecords = soundFont.shdrChunk.shdrRecords
            .filter { shdrRecord -> shdrRecord.achSampleName.startsWith("P200") }
//            .forEach { shdrRecord ->
//                println(shdrRecord)
//            }

        val sampleData = soundFont.smplChunk ?: throw IllegalStateException("Sample chunk not found")
        val smplData = sampleData.smplData

        var currentSampleOffset = 0
        val extractedSamples = mutableListOf<ByteArray>()
        val updatedShdrRecords = pianoShdrRecords.map { shdrRecord ->
            val startByte = 8 + shdrRecord.dwStart * 2
            val endByte = 8 + shdrRecord.dwEnd * 2
            val part = smplData.copyOfRange(startByte, endByte)
            extractedSamples.add(part)

            val newStart = currentSampleOffset
            val sizeInSamples = shdrRecord.dwEnd - shdrRecord.dwStart
            val newEnd = newStart + sizeInSamples

            val startLoopOffset = shdrRecord.dwStartloop - shdrRecord.dwStart
            val endLoopOffset = shdrRecord.dwEndloop - shdrRecord.dwStart

            val updatedRecord = shdrRecord.copy(
                dwStart = newStart,
                dwEnd = newEnd,
                dwStartloop = newStart + startLoopOffset,
                dwEndloop = newStart + endLoopOffset
            )
            currentSampleOffset += sizeInSamples
            updatedRecord
        }

        val totalDataSize = currentSampleOffset * 2
        val newSmplData = ByteArray(8 + totalDataSize)
        "smpl".toByteArray().copyInto(newSmplData, 0)
        newSmplData[4] = (totalDataSize and 0xFF).toByte()
        newSmplData[5] = ((totalDataSize shr 8) and 0xFF).toByte()
        newSmplData[6] = ((totalDataSize shr 16) and 0xFF).toByte()
        newSmplData[7] = ((totalDataSize shr 24) and 0xFF).toByte()

        var currentByteOffset = 8
        for (part in extractedSamples) {
            part.copyInto(newSmplData, currentByteOffset)
            currentByteOffset += part.size
        }

        val newShdrChunk = com.kjipo.soundfontparser.ShdrChunk(updatedShdrRecords)
        val newSmplChunk = com.kjipo.soundfontparser.SmplChunk(newSmplData)

        println("Original samples: ${smplData.size} bytes")
        println("Filtered samples: ${newSmplData.size} bytes")

        SoundFontData(
            newShdrChunk,
            InstSubchunk(emptyList()),
            PhdrSubchunk(emptyList()),
            newSmplChunk
        )
    }


    val serializedSoundFontData = SoundFontDataJsonSerializer.serialize(soundFontData)
    File("filtered_soundfont.json").writeText(serializedSoundFontData)
}


fun main() {
    filterSoundFont()

//    val soundFontData = runBlocking {
////        val soundfontData = loadFile("florestan-subset.sf2")
////        val soundfontData = loadFile("merlin_gold.sf2")
//        val soundfontData = loadFile("FluidR3Mono_GM2-315.SF2")
//        val chunks = Parser.parse(soundfontData)
//        printChunks(chunks)
//        SetupData.setupData(chunks, soundfontData)
//    }
//
//    val samplePlayer = SamplePlayer(soundFontData)
//    samplePlayer.playSample(1)

}



