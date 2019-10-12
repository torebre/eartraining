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

fun playNote() {
    val context = AudioContext()
    println(context.sampleRate)
    val osc = context.createOscillator()
    osc.connect(context.destination)
    osc.start()

}