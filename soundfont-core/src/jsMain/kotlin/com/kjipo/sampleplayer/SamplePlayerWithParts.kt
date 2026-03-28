package com.kjipo.sampleplayer


actual fun playSampleData(sampleData: SampleDataWithParts): PitchOnReference {
    val audioContext: dynamic = js("new (window.AudioContext || window.webkitAudioContext)()")

    val totalSize = sampleData.startSample.size + sampleData.repeatSection.size + sampleData.endSection.size
    val float32Array: dynamic = js("new Float32Array(totalSize)")

    var index = 0
    for (i in 0 until sampleData.startSample.size) {
        float32Array[index++] = sampleData.startSample[i] / 32768.0
    }
    for (i in 0 until sampleData.repeatSection.size) {
        float32Array[index++] = sampleData.repeatSection[i] / 32768.0
    }
    for (i in 0 until sampleData.endSection.size) {
        float32Array[index++] = sampleData.endSection[i] / 32768.0
    }

    val buffer: dynamic = audioContext.createBuffer(1, totalSize, sampleData.sampleRate)
    val channelData: dynamic = buffer.getChannelData(0)
    channelData.set(float32Array)

    val source: dynamic = audioContext.createBufferSource()
    source.buffer = buffer
    source.detune.value = sampleData.chPitchCorrection.toDouble()
    source.playbackRate.value = sampleData.playbackRate

    if (sampleData.repeatSection.size > 0) {
        source.loop = true
        source.loopStart = sampleData.startSample.size.toDouble() / sampleData.sampleRate
        source.loopEnd = (sampleData.startSample.size + sampleData.repeatSection.size).toDouble() / sampleData.sampleRate
    }

    source.connect(audioContext.destination)
    source.start(0)

    return object : PitchOnReference {
        override fun pitchOff() {
            source.loop = false
        }
    }
}
