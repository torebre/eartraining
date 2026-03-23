package com.kjipo.sampleplayer


actual fun playSampleData(sampleData: SampleData)  {
    val audioContext: dynamic = js("new (window.AudioContext || window.webkitAudioContext)()")

    val numSamples = sampleData.sampleData.size
    val float32Array: dynamic = js("new Float32Array(numSamples)")

    for (i in 0 until numSamples) {
        // Normalize to [-1.0, 1.0] range
        float32Array[i] = sampleData.sampleData[i] / 32768.0
    }

    // Create buffer with the correct number of samples
    val buffer: dynamic = audioContext.createBuffer(1, numSamples, sampleData.sampleRate)
    val channelData: dynamic = buffer.getChannelData(0)

    // Copy Float32Array data to buffer
    channelData.set(float32Array)

    // Create source and play
    val source: dynamic = audioContext.createBufferSource()
    source.buffer = buffer
    source.detune.value = sampleData.chPitchCorrection.toDouble()
    source.playbackRate.value = sampleData.playbackRate
    source.connect(audioContext.destination)
    source.start(0)
}

fun playScale(samplePlayer: SamplePlayer, sampleNumber: Int) {
    val audioContext: dynamic = js("new (window.AudioContext || window.webkitAudioContext)()")
    val scaleData = samplePlayer.playScale(sampleNumber)

    var startTime = 0.0

    scaleData.forEach { sampleData ->
        val numSamples = sampleData.sampleData.size
        val float32Array: dynamic = js("new Float32Array(numSamples)")

        for (i in 0 until numSamples) {
            // Normalize to [-1.0, 1.0] range
            float32Array[i] = sampleData.sampleData[i] / 32768.0
        }

        // Create buffer with the correct number of samples
        val buffer: dynamic = audioContext.createBuffer(1, numSamples, sampleData.sampleRate)
        val channelData: dynamic = buffer.getChannelData(0)

        // Copy Float32Array data to buffer
        channelData.set(float32Array)

        // Create source and schedule to play
        val source: dynamic = audioContext.createBufferSource()
        source.buffer = buffer
        source.detune.value = sampleData.chPitchCorrection.toDouble()
        source.playbackRate.value = sampleData.playbackRate
        source.connect(audioContext.destination)
        source.start(startTime)

        // Calculate duration and move start time forward
        val duration = numSamples.toDouble() / sampleData.sampleRate
        startTime += duration
    }
}


fun playSampleDataLoop() {
    val ctx: dynamic = js("new (window.AudioContext || window.webkitAudioContext)()")

    val source: dynamic = ctx.createBufferSource()
//    source.buffer = buffer

// loop points in seconds
//    source.loop = true
//    source.loopStart = loopStartSamples.toDouble() / sampleRate
//    source.loopEnd = loopEndSamples.toDouble() / sampleRate

    val gain: dynamic = ctx.createGain()
    gain.gain.value = 0.0

    source.connect(gain)
    gain.connect(ctx.destination)

// key down
    val now = ctx.currentTime
    gain.gain.cancelScheduledValues(now)
    gain.gain.setValueAtTime(gain.gain.value, now)
    gain.gain.linearRampToValueAtTime(1.0, now + 0.01) // attack
    source.start(now)

// key up
    val releaseStart = ctx.currentTime
    gain.gain.cancelScheduledValues(releaseStart)
    gain.gain.setValueAtTime(gain.gain.value, releaseStart)
    gain.gain.linearRampToValueAtTime(0.0, releaseStart + 0.15) // release
    source.stop(releaseStart + 0.16)
}
