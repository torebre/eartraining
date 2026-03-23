package com.kjipo.sampleplayer

import com.kjipo.soundfontparser.ShdrRecord
import com.kjipo.soundfontparser.SoundFontData
import kotlin.math.pow


expect fun playSampleData(sampleData: SampleData)


class SamplePlayer {
    private val soundFontData: SoundFontData
    private val dataInSamples: IntArray
    private val pitchToSampleMap: Map<Int, ShdrRecord>


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

    fun playSample(sampleNumber: Int) {
        val shdrRecord = soundFontData.shdrChunk.shdrRecords[sampleNumber]
        val durationInSeconds = 2

        val sampleData = createSample(shdrRecord, durationInSeconds, 1.0)

        playSampleData(sampleData)
    }

    fun createSample(sampleNumber: Int, durationInSeconds: Int, playbackRate: Double): SampleData {
        val shdrRecord = soundFontData.shdrChunk.shdrRecords[sampleNumber]
        return createSample(shdrRecord, durationInSeconds, playbackRate)
    }

    private fun createSample(shdrRecord: ShdrRecord, requestedDurationInSeconds: Int, playbackRate: Double): SampleData {
        val samplePointsNeeded = requestedDurationInSeconds * shdrRecord.dwSampleRate * playbackRate.toInt()

        val startSegment = dataInSamples.copyOfRange(shdrRecord.dwStart, shdrRecord.dwStartloop)
        val loopSegment = dataInSamples.copyOfRange(shdrRecord.dwStartloop, shdrRecord.dwEndloop)
        // TODO Make use of endSegment when setting up the data to play
        val endSegment = dataInSamples.copyOfRange(shdrRecord.dwEndloop, shdrRecord.dwEnd)

        var samplesRemaining = samplePointsNeeded
        samplesRemaining -= startSegment.size

        if(samplesRemaining <= 0) {
            // TODO Check that this is the correct number of sample points to return when the requested duration is shorter than the start segment of the sample
            return SampleData(dataInSamples.copyOfRange(shdrRecord.dwStart, shdrRecord.dwStart + samplePointsNeeded),
                shdrRecord.dwSampleRate, shdrRecord.chPitchCorrection, playbackRate)
        }

        samplesRemaining -= endSegment.size
        if(samplesRemaining <= 0) {
            return SampleData(startSegment + endSegment, shdrRecord.dwSampleRate, shdrRecord.chPitchCorrection, playbackRate)
        }

        val numberOfLoopSegmentsNeeded = if(samplesRemaining % loopSegment.size > 0) {
            samplesRemaining / loopSegment.size + 1
        }
        else {
            samplesRemaining / loopSegment.size
        }

        val dataInSamplesForDuration = IntArray(startSegment.size + loopSegment.size * numberOfLoopSegmentsNeeded + endSegment.size)
        startSegment.copyInto(dataInSamplesForDuration, 0, 0, startSegment.size)
        for(i in 0 until numberOfLoopSegmentsNeeded) {
            loopSegment.copyInto(dataInSamplesForDuration, startSegment.size + i * loopSegment.size, 0, loopSegment.size)
        }
        endSegment.copyInto(dataInSamplesForDuration, startSegment.size + loopSegment.size * numberOfLoopSegmentsNeeded, 0, endSegment.size)

        return SampleData(dataInSamplesForDuration, shdrRecord.dwSampleRate, shdrRecord.chPitchCorrection, playbackRate)
    }

    fun playScale(sampleNumber: Int): List<SampleData> {
        val shdrRecord = soundFontData.shdrChunk.shdrRecords[sampleNumber]
        return playScale(shdrRecord)
    }


    fun playScale(shdrRecord: ShdrRecord): List<SampleData> {
        val notesInScale = 13 // Chromatic scale (12 semitones + octave)
        val noteDurationInSeconds = 2

        val samples = mutableListOf<SampleData>()
        for (noteIndex in 0 until notesInScale) {
            val semitones = noteIndex
            val playbackRate = 2.0.pow(semitones / 12.0)

            createSample(shdrRecord, noteDurationInSeconds, playbackRate).let { samples.add(it) }
        }

        return samples
    }


    fun playPitch(midiKey: Int, durationInSeconds: Int = 2) {
        generatePitchSample(midiKey, durationInSeconds)?.let { playSampleData(it) }
    }

    fun generatePitchSample(midiKey: Int, durationInSeconds: Int): SampleData? {
        val closestPitch = pitchToSampleMap.keys
            .filter { it <= midiKey }
            .maxOrNull() ?: return null

        val shdrRecord = pitchToSampleMap[closestPitch] ?: return null
        val semitones = midiKey - closestPitch
        val playbackRate = 2.0.pow(semitones / 12.0)

        return createSample(shdrRecord, durationInSeconds, playbackRate)
    }



    private fun createSamples(smplData: ByteArray): IntArray {
        val dataInSamples = IntArray(smplData.size / 2 - 4)
        for (i in 0 until dataInSamples.size) {
            val byteIndex = i * 2
            // Read 16-bit little-endian signed integer
            val low = smplData[byteIndex + 8].toInt() and 0xFF
            val high = smplData[byteIndex + 1 + 8].toInt()
            val sample16 = (high shl 8) or low

            // Convert to signed 16-bit
            val signedSample = if (sample16 > 32767) sample16 - 65536 else sample16
            dataInSamples[i] = signedSample
        }

        return dataInSamples
    }


}
