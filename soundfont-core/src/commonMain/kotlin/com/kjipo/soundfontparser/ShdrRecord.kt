package com.kjipo.soundfontparser


/**
 * Represents a sample header record from the SHDR sub-chunk of a SoundFont file.
 *
 * @property achSampleName Sample name (20 ASCII chars, null-terminated). Names are case-sensitive and should be unique.
 * @property dwStart Index in sample data points from the beginning to the first data point of this sample.
 * @property dwEnd Index in sample data points from the beginning to the first of 46 zero valued data points following this sample.
 * @property dwStartloop Index in sample data points from the beginning to the first data point in the loop.
 * @property dwEndloop Index in sample data points from the beginning to the first data point following the loop.
 * @property dwSampleRate Sample rate in hertz (Hz). Valid range: 400-50000 Hz.
 * @property byOriginalPitch MIDI key number of the recorded pitch (e.g., 60 for middle C). Use 255 for unpitched sounds.
 * @property chPitchCorrection Pitch correction in cents to apply on playback (e.g., -4 if sample is 4 cents sharp).
 * @property wSampleLink Sample header index of linked stereo pair (left/right), or 0 for mono samples.
 * @property sfSampleType Sample type enumeration: monoSample=1, rightSample=2, leftSample=4, linkedSample=8,
 *                        RomMonoSample=32769, RomRightSample=32770, RomLeftSample=32772, RomLinkedSample=32776.
 *                        Bit 15 indicates ROM sample, lower 4 bits indicate mono/left/right/linked.
 */
data class ShdrRecord(
    val achSampleName: String,
    val dwStart: Int,
    val dwEnd: Int,
    val dwStartloop: Int,
    val dwEndloop: Int,
    val dwSampleRate: Int,
    val byOriginalPitch: Byte,
    val chPitchCorrection: Int,
    val wSampleLink: Int,
    val sfSampleType: SampleType
) {

    override fun toString(): String {
        return "ShdrRecord(achSampleName='$achSampleName', dwStart=$dwStart, dwEnd=$dwEnd, dwStartloop=$dwStartloop, dwEndloop=$dwEndloop, dwSampleRate=$dwSampleRate, byOriginalPitch=$byOriginalPitch, chPitchCorrection='$chPitchCorrection', wSampleLink=$wSampleLink, sfSampleType=$sfSampleType)"
    }

}
