package com.kjipo.sampleplayer

import com.kjipo.soundfontparser.InstSubchunk
import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.PhdrSubchunk
import com.kjipo.soundfontparser.SetupData
import com.kjipo.soundfontparser.ShdrChunk
import com.kjipo.soundfontparser.ShdrRecord
import com.kjipo.soundfontparser.SoundFontData
import com.kjipo.soundfontparser.loadFile
import com.kjipo.soundfontparser.serialization.SoundFontDataJsonSerializer
import kotlinx.coroutines.runBlocking
import java.io.File


/**
 * This method is used to generate filtered_soundfont.json, which is based on FluidR3Mono_GM2-315.SF2. It extracts
 * the piano samples from that soundfont file and writes the data to a JSON-file.
 */
private fun filterSoundFont() {
    val soundFontData = runBlocking {
        val soundfontData = loadFile("FluidR3Mono_GM2-315.SF2")
        val chunks = Parser.parse(soundfontData)
        val soundFont = SetupData.setupData(chunks, soundfontData)

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

            val newStart = currentSampleOffset
            val sizeInSamples = shdrRecord.dwEnd - shdrRecord.dwStart

            extractedSamples.add(part)
            currentSampleOffset += sizeInSamples

            val newEnd = newStart + sizeInSamples
            updateRecord(shdrRecord, newStart, newEnd)
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

        val newShdrChunk = ShdrChunk(updatedShdrRecords).shdrRecords.map { shdrRecord ->
            shdrRecord.copy(byOriginalPitch = parseSampleName(shdrRecord.achSampleName))
        }.let { ShdrChunk(it) }
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


private fun updateRecord(shdrRecord: ShdrRecord, newStart: Int, newEnd: Int): ShdrRecord {
    val startLoopOffset = shdrRecord.dwStartloop - shdrRecord.dwStart
    val endLoopOffset = shdrRecord.dwEndloop - shdrRecord.dwStart

    return shdrRecord.copy(
        dwStart = newStart,
        dwEnd = newEnd,
        dwStartloop = newStart + startLoopOffset,
        dwEndloop = newStart + endLoopOffset
    )
}

private fun parseSampleName(sampleName: String): Byte {
    val noteName = sampleName.substring(11, sampleName.lastIndexOf('('))

    return if(noteName.length == 2) {
        getPitchFromNoteNameAndOctave(noteName.substring(0, 1), Integer.parseInt(noteName.substring(1))).toByte()
    }
    else {
        val firstDigitIndex = noteName.indices.firstNotNullOf { i ->
            if (noteName[i].isDigit()) {
                i
            } else {
                null
            }
        }

        val octave = noteName.substring(firstDigitIndex)
        val noteNameWithoutOctave = noteName.substring(0, firstDigitIndex)
        getPitchFromNoteNameAndOctave(noteNameWithoutOctave, Integer.parseInt(octave)).toByte()
    }

}


fun main() {
    filterSoundFont()

}
