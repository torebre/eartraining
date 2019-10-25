external class AudioContext {
    val sampleRate: Float
    val destination: AudioNode
    fun createOscillator(): OscillatorNode
}


external open class AudioNode {
    fun connect(destination: AudioNode, output: Int = definedExternally, input: Int = definedExternally): AudioNode

}

external class OscillatorNode : AudioNode {
    fun start(time: Double = definedExternally)

}


external class Tone {

    class Sampler(sample: Any, onLoad: Any) {

        var loaded: Boolean
        var volume: Boolean

        fun triggerAttack(any: Any)

        fun triggerAttackRelease(any: Any, durationInSeconds: Double)
        fun triggerAttackRelease(any: Any, durationInSeconds: Double, timeStart: Double)

        fun sync()

        fun toMaster(): Any

    }

    class Buffer(buffer: Any, onLoad: Any) {
        val duration: Number

        companion object {
            fun on(function: String, callback: Any)
        }

    }

    class Transport {

        companion object {
            fun start()

            fun stop()

        }


    }

}



fun playNote(sampler: Tone.Sampler) {

    console.log("Test23. Loaded: ${sampler.loaded}. Volume: ${sampler.volume}")

    Tone.Transport.stop()

    sampler.sync()

    sampler.triggerAttackRelease("C3", 0.5)
    sampler.triggerAttackRelease("C#3", 0.5, 0.5)
    sampler.triggerAttackRelease("D3", 0.5, 1.0)
    sampler.triggerAttackRelease("D#3", 0.5, 1.5)
    sampler.triggerAttackRelease("E3", 0.5, 2.0)
    sampler.triggerAttackRelease("F3", 0.5, 2.5)
    sampler.triggerAttackRelease("F#3", 0.5, 3.0)
    sampler.triggerAttackRelease("G3", 0.5, 3.5)
    sampler.triggerAttackRelease("G#3", 0.5, 4.0)
    sampler.triggerAttackRelease("A3", 0.5, 4.5)
    sampler.triggerAttackRelease("A#3", 0.5, 5.0)
    sampler.triggerAttackRelease("B3", 0.5, 5.5)
    sampler.triggerAttackRelease("C4", 0.5, 6.0)

    Tone.Transport.start()

//    Tone.Buffer.on("load", {
//
//        console.log("Test30. Loaded: ${sampler.loaded}. Volume: ${sampler.volume}")
//
//        sampler.triggerAttack("C1")
//
//    })


}

