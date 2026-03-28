package com.kjipo.sampleplayer

import com.kjipo.soundfontparser.ShdrRecord
import com.kjipo.soundfontparser.SoundFontData
import kotlin.math.pow


expect fun playSampleData(sampleData: SampleDataWithParts): PitchOnReference


class SamplePlayerInteractive {
    private val soundFontData: SoundFontData
    private val dataInSamples: IntArray
    private val pitchToSampleMap: Map<Int, ShdrRecord>

    private val activePitches = mutableMapOf<Int, PitchOnReference>()


    constructor(soundFontData: SoundFontData) {
        this.soundFontData = soundFontData
        // TODO Is it possible to avoid removing the null check?
        soundFontData.smplChunk!!.let { smplChunk ->
            dataInSamples = createSamples(smplChunk.smplData)
        }

        pitchToSampleMap = soundFontData.shdrChunk.shdrRecords
            .filter { it.byOriginalPitch.toInt() != 255 }
            .associateBy { it.byOriginalPitch.toInt() }
    }

    fun getSampleNames(): List<String> {
        return soundFontData.shdrChunk.shdrRecords.map { it.achSampleName }
    }

    fun getSampleHeaderData(): List<ShdrRecord> {
        return soundFontData.shdrChunk.shdrRecords
    }

    fun createSampleWithParts(shdrRecord: ShdrRecord, playbackRate: Double): SampleDataWithParts {
        val startSegment = dataInSamples.copyOfRange(shdrRecord.dwStart, shdrRecord.dwStartloop)
        val loopSegment = dataInSamples.copyOfRange(shdrRecord.dwStartloop, shdrRecord.dwEndloop)
        val endSegment = dataInSamples.copyOfRange(shdrRecord.dwEndloop, shdrRecord.dwEnd)

        return SampleDataWithParts(
            startSegment,
            loopSegment,
            endSegment,
            shdrRecord.dwSampleRate,
            shdrRecord.chPitchCorrection,
            playbackRate
        )
    }

    private fun generatePitchSample(midiKey: Int): SampleDataWithParts? {
        val closestPitch = pitchToSampleMap.keys
            .filter { it <= midiKey }
            .maxOrNull() ?: return null

        val shdrRecord = pitchToSampleMap[closestPitch] ?: return null
        val semitones = midiKey - closestPitch
        val playbackRate = 2.0.pow(semitones / 12.0)

        return createSampleWithParts(shdrRecord, playbackRate)
    }

    fun pitchOn(pitch: Int) {
        generatePitchSample(pitch)?.let {
            activePitches[pitch] = playSampleData(it)
        }
    }

    fun pitchOff(pitch: Int) {
        activePitches.remove(pitch)?.pitchOff()
    }


}
