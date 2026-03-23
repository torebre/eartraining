package com.kjipo.soundfontparser

data class GeneratorData(
    val number: Int,
    val name: String,
    val unit: String,
    val absZero: String,
    val min: String,
    val minUseful: String,
    val max: String,
    val maxUseful: String,
    val default: String,
    val defValue: String
)

object Generators {
    private val generatorDataMap = mapOf(
        0 to GeneratorData(0, "startAddrsOffset", "+ smpls", "0", "0", "None", "*", "*", "0", "None"),
        1 to GeneratorData(1, "endAddrsOffset", "+ smpls", "0", "*", "*", "0", "None", "0", "None"),
        2 to GeneratorData(2, "startloopAddrsOffset", "+ smpls", "0", "*", "*", "*", "*", "0", "None"),
        3 to GeneratorData(3, "endloopAddrsOffset", "+ smpls", "0", "*", "*", "*", "*", "0", "None"),
        4 to GeneratorData(4, "startAddrsCoarseOffset", "+ 32k smpls", "0", "0", "None", "*", "*", "0", "None"),
        5 to GeneratorData(5, "modLfoToPitch", "cent fs", "0", "-12000", "-10 oct", "12000", "10 oct", "0", "None"),
        6 to GeneratorData(6, "vibLfoToPitch", "cent fs", "0", "-12000", "-10 oct", "12000", "10 oct", "0", "None"),
        7 to GeneratorData(7, "modEnvToPitch", "cent fs", "0", "-12000", "-10 oct", "12000", "10 oct", "0", "None"),
        8 to GeneratorData(8, "initialFilterFc", "cent", "8.176 Hz", "1500", "20 Hz", "13500", "20 kHz", "13500", "Open"),
        9 to GeneratorData(9, "initialFilterQ", "cB", "0", "0", "None", "960", "96 dB", "0", "None"),
        10 to GeneratorData(10, "modLfoToFilterFc", "cent fs", "0", "-12000", "-10 oct", "12000", "10 oct", "0", "None"),
        11 to GeneratorData(11, "modEnvToFilterFc", "cent fs", "0", "-12000", "-10 oct", "12000", "10 oct", "0", "None"),
        12 to GeneratorData(12, "endAddrsCoarseOffset", "+ 32k smpls", "0", "*", "*", "0", "None", "0", "None"),
        13 to GeneratorData(13, "modLfoToVolume", "cB fs", "0", "-960", "-96 dB", "960", "96 dB", "0", "None"),
        15 to GeneratorData(15, "chorusEffectsSend", "0.1%", "0", "0", "None", "1000", "100%", "0", "None"),
        16 to GeneratorData(16, "reverbEffectsSend", "0.1%", "0", "0", "None", "1000", "100%", "0", "None"),
        17 to GeneratorData(17, "pan", "0.1%", "Cntr", "-500", "Left", "+500", "Right", "0", "Center"),
        21 to GeneratorData(21, "delayModLFO", "timecent", "1 sec", "-12000", "1 msec", "5000", "20 sec", "-12000", "<1 msec"),
        22 to GeneratorData(22, "freqModLFO", "cent", "8.176 Hz", "-16000", "1 mHz", "4500", "100 Hz", "0", "8.176 Hz"),
        23 to GeneratorData(23, "delayVibLFO", "timecent", "1 sec", "-12000", "1 msec", "5000", "20 sec", "-12000", "<1 msec"),
        24 to GeneratorData(24, "freqVibLFO", "cent", "8.176 Hz", "-16000", "1 mHz", "4500", "100 Hz", "0", "8.176 Hz"),
        25 to GeneratorData(25, "delayModEnv", "timecent", "1 sec", "-12000", "1 msec", "5000", "20 sec", "-12000", "<1 msec"),
        26 to GeneratorData(26, "attackModEnv", "timecent", "1 sec", "-12000", "1 msec", "8000", "100sec", "-12000", "<1 msec"),
        27 to GeneratorData(27, "holdModEnv", "timecent", "1 sec", "-12000", "1 msec", "5000", "20 sec", "-12000", "<1 msec"),
        28 to GeneratorData(28, "decayModEnv", "timecent", "1 sec", "-12000", "1 msec", "8000", "100sec", "-12000", "<1 msec"),
        29 to GeneratorData(29, "sustainModEnv", "-0.1%", "attk peak", "0", "100%", "1000", "0%", "0", "attk pk"),
        30 to GeneratorData(30, "releaseModEnv", "timecent", "1 sec", "-12000", "1 msec", "8000", "100sec", "-12000", "<1 msec"),
        31 to GeneratorData(31, "keynumToModEnvHold", "tcent/key", "0", "-1200", "-oct/ky", "1200", "oct/ky", "0", "None"),
        32 to GeneratorData(32, "keynumToModEnvDecay", "tcent/key", "0", "-1200", "-oct/ky", "1200", "oct/ky", "0", "None"),
        33 to GeneratorData(33, "delayVolEnv", "timecent", "1 sec", "-12000", "1 msec", "5000", "20 sec", "-12000", "<1 msec"),
        34 to GeneratorData(34, "attackVolEnv", "timecent", "1 sec", "-12000", "1 msec", "8000", "100sec", "-12000", "<1 msec"),
        35 to GeneratorData(35, "holdVolEnv", "timecent", "1 sec", "-12000", "1 msec", "5000", "20 sec", "-12000", "<1 msec"),
        36 to GeneratorData(36, "decayVolEnv", "timecent", "1 sec", "-12000", "1 msec", "8000", "100sec", "-12000", "<1 msec"),
        37 to GeneratorData(37, "sustainVolEnv", "cB attn", "attk peak", "0", "0 dB", "1440", "144dB", "0", "attk pk"),
        38 to GeneratorData(38, "releaseVolEnv", "timecent", "1 sec", "-12000", "1 msec", "8000", "100sec", "-12000", "<1 msec"),
        39 to GeneratorData(39, "keynumToVolEnvHold", "tcent/key", "0", "-1200", "-oct/ky", "1200", "oct/ky", "0", "None"),
        40 to GeneratorData(40, "keynumToVolEnvDecay", "tcent/key", "0", "-1200", "-oct/ky", "1200", "oct/ky", "0", "None"),
        43 to GeneratorData(43, "keyRange", "@ MIDI ky#", "key# 0", "0", "lo key", "127", "hi key", "0-127", "full kbd"),
        44 to GeneratorData(44, "velRange", "@ MIDI vel", "0", "0", "min vel", "127", "max vel", "0-127", "all vels"),
        45 to GeneratorData(45, "startloopAddrsCoarseOffset", "+ smpls", "0", "*", "*", "*", "*", "0", "None"),
        46 to GeneratorData(46, "keynum", "+@ MIDI ky#", "key# 0", "0", "lo key", "127", "hi key", "-1", "None"),
        47 to GeneratorData(47, "velocity", "+@ MIDI vel", "0", "1", "min vel", "127", "mx vel", "-1", "None"),
        48 to GeneratorData(48, "initialAttenuation", "cB", "0", "0", "0 dB", "1440", "144dB", "0", "None"),
        50 to GeneratorData(50, "endloopAddrsCoarseOffset", "+ smpls", "0", "*", "*", "*", "*", "0", "None"),
        51 to GeneratorData(51, "coarseTune", "semitone", "0", "-120", "-10 oct", "120", "10 oct", "0", "None"),
        52 to GeneratorData(52, "fineTune", "cent", "0", "-99", "- 99cent", "99", "99cent", "0", "None"),
        54 to GeneratorData(54, "sampleModes", "+@ Bit Flags", "Flags", "**", "**", "**", "**", "0", "No Loop"),
        56 to GeneratorData(56, "scaleTuning", "@ cent/key", "0", "0", "none", "1200", "oct/ky", "100", "semi-tone"),
        57 to GeneratorData(57, "exclusiveClass", "+@ arbitrary#", "0", "1", "--", "127", "--", "0", "None"),
        58 to GeneratorData(58, "overridingRootKey", "+@ MIDI ky#", "key# 0", "0", "lo key", "127", "hi key", "-1", "None")
    )

    fun getGeneratorData(number: Int): GeneratorData? {
        return generatorDataMap[number]
    }
}
