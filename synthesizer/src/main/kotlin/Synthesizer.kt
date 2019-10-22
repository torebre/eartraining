external class AudioContext {
    val sampleRate: Float
    val destination: AudioNode
    fun createOscillator(): OscillatorNode
}


external open class AudioNode {
    fun connect(destination: AudioNode, output: Int = definedExternally, input: Int = definedExternally): AudioNode

}

external class OscillatorNode: AudioNode {
    fun start(time: Double = definedExternally)

}




external class Tone {

    class Sampler(sample: Any, onLoad: Any){

        fun triggerAttack(any: Any)

        fun loaded(): Boolean

        fun volume(): Any

    }

    class Buffer(buffer: Any, onLoad: Any) {
        val duration: Number

        companion object {
            fun on(function: String, callback: Any)
        }

    }

}

fun playNote() {
//    val context = AudioContext()
//    println(context.sampleRate)
//    val osc = context.createOscillator()
//    osc.connect(context.destination)
//    osc.start()

    var sampler: Tone.Sampler? = null
//    var buffer = Tone.Buffer("samples/UR1_C1_f_RR1.wav", {
    var buffer = Tone.Buffer("samples/chord.mp3", {
        val samples : dynamic = Any()
//        samples.C1 = "samples/UR1_C1_f_RR1.wav"
        samples.C1 = "samples/chord.mp3"

         sampler = Tone.Sampler(samples, {
            console.log("Test24")


        })



    })

    console.log("Test25. Duration: ${buffer.duration}")


    Tone.Buffer.on("load", {

        console.log("Test23. Loaded: ${sampler!!.loaded()}. Volume: ${sampler!!.volume()}")

        sampler!!.triggerAttack("C1")

    })




}

