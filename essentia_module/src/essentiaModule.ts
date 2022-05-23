import {EssentiaWASM} from './essentia/essentia-wasm.module.js'
import {Essentia} from './essentia/essentia.js-core.js'
import {ChromeLabsRingBuffer} from "./ChromeLabsRingBuffer";
import InternalAudioBuffer from "./internalAudioBuffer";


/**
 * https://developer.mozilla.org/en-US/docs/Web/API/AudioWorkletProcessor
 */
class TestAudioProcessor extends AudioWorkletProcessor {
    private bufferSize: number
    private sampleRate: number
    private channelCount = 1
    private rmsThreshold = 0.04
    private frameSize: number
    private hopSize: number
    private essentia: Essentia
    private lowestFreq = 440 * Math.pow(Math.pow(2, 1 / 12), -33) // lowest note = C2
    private highestFreq = 440 * Math.pow(Math.pow(2, 1 / 12), -33 + (6 * 12) - 1) // 6 octaves above C2

    private inputRingBuffer: ChromeLabsRingBuffer
    private accumData: any
    private audioWriter: InternalAudioBuffer

    constructor(options) {
        super()

        this.bufferSize = options.processorOptions.bufferSize
        this.sampleRate = options.processorOptions.sampleRate
        this.essentia = new Essentia(EssentiaWASM)
        this.frameSize = this.bufferSize / 2
        this.hopSize = this.frameSize / 4
        this.inputRingBuffer = new ChromeLabsRingBuffer(this.bufferSize, this.channelCount)

        this.accumData = [new Float32Array(this.bufferSize)]

        // SAB config
        this.port.onmessage = e => {
            this.audioWriter = new InternalAudioBuffer(e.data.sab)
        };
    }

    process(inputList, outputList, params) {
        const input = inputList[0]
        // let output = outputList[0];

        // console.log("Input: " + input)

        this.inputRingBuffer.push(input)

        // console.log("Test26: " +this.inputRingBuffer.framesAvailable)

        if (this.inputRingBuffer.framesAvailable >= this.bufferSize) {
            this.inputRingBuffer.pull(this.accumData)

            const accumDataVector = this.essentia.arrayToVector(this.accumData[0])
            const rms = this.essentia.RMS(accumDataVector).rms

            // const algoOutput = this._essentia.PitchYinProbabilistic(essentia.arrayToVector(this._accumData[0]), this._frameSize, this._hopSize, this._rmsThreshold, "zero", false, this._sampleRate)
            const algoOutput = this.essentia.PitchMelodia(
                accumDataVector,
                10, 3, this.frameSize, false, 0.8, this.hopSize, 1, 40, this.highestFreq, 100, this.lowestFreq, 20, 0.9, 0.9, 27.5625, this.lowestFreq, this.sampleRate, 100
            )

            const pitchFrames = this.essentia.vectorToArray(algoOutput.pitch)
            const confidenceFrames = this.essentia.vectorToArray(algoOutput.pitchConfidence)

            // average frame-wise pitches in pitch before writing to SAB
            const numVoicedFrames = pitchFrames.filter(p => p > 0).length
            // const numFrames = pitchFrames.length
            const meanPitch = pitchFrames.reduce((acc, val) => acc + val, 0) / numVoicedFrames
            const meanConfidence = confidenceFrames.reduce((acc, val) => acc + val, 0) / numVoicedFrames
            console.info("audio: ", meanPitch, meanConfidence, rms)
            // write to SAB using AudioWriter object so that pitch output can be accesed from the main UI thread
            if (this.audioWriter.availableWrite() >= 1) {
                this.audioWriter.push([meanPitch, meanConfidence, rms])
            }

            // reset variables
            this.accumData = [new Float32Array(this.bufferSize)]
        }
        return true
    }

}


registerProcessor("test-audio-processor", TestAudioProcessor)