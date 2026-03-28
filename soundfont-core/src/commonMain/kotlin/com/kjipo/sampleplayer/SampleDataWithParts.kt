package com.kjipo.sampleplayer

class SampleDataWithParts(
    val startSample: IntArray,
    val repeatSection: IntArray,
    val endSection: IntArray,
    val sampleRate: Int,
    val chPitchCorrection: Int,
    val playbackRate: Double
)