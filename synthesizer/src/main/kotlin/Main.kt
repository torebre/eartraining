import kotlin.browser.document


fun main() {
//
//    val samples: dynamic = Any()
////        samples.C1 = "samples/UR1_C1_f_RR1.wav"
////    samples.C1 = "chord.mp3"
//    samples.C1 = "UR1_C1_f_RR1.wav"
//    samples.C2 = "UR1_C2_f_RR1.wav"
//    samples.C3 = "UR1_C3_f_RR1.wav"
//    samples.C4 = "UR1_C4_f_RR1.wav"
//    samples.C5 = "UR1_C5_f_RR1.wav"
//    samples.C6 = "UR1_C6_f_RR1.wav"
//    samples.C7 = "UR1_C7_f_RR1.wav"
////    samples.G2 = "UR1_G2_f_RR1.wav"
////    samples.G3 = "UR1_G3_f_RR1.wav"
////    samples.G4 = "UR1_G4_f_RR1.wav"
////    samples.G5 = "UR1_G5_f_RR1.wav"
////    samples.G6 = "UR1_G6_f_RR1.wav"
////    samples.G7 = "UR1_G7_f_RR1.wav"
//
//    val parameters: dynamic = Any()
//    parameters.release = 1
//    parameters.baseUrl = "samples/"
//
//    var sampler = Tone.Sampler(samples, parameters)
//    sampler.toMaster()
//
//    Tone.Transport.start()

    val synthesizer = SynthesizerScript()
    var notePlaying = false

    document.querySelector("button")!!.addEventListener("click", {
        if(notePlaying) {
            synthesizer.noteOff(60)
        }
        else {
            synthesizer.noteOn(60)
        }

        notePlaying = !notePlaying
    })
}
