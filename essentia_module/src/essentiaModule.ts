import {EssentiaWASM} from './essentia/essentia-wasm.module.js'
import {Essentia} from './essentia/essentia.js-core.js'
import {ChromeLabsRingBuffer} from "./ChromeLabsRingBuffer";


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
    private audioWriter: AudioBuffer

    constructor(options) {
        super()

        this.bufferSize = options.processorOptions.bufferSize
        this.sampleRate = options.processorOptions.sampleRate
        this.essentia = new Essentia(EssentiaWASM)
        this.frameSize = this.bufferSize / 2
        this.hopSize = this.frameSize / 4

        // buffersize mismatch helpers
        this.inputRingBuffer = new ChromeLabsRingBuffer(this.bufferSize, this.channelCount)

        this.accumData = [new Float32Array(this.bufferSize)]

        // SAB config
        this.port.onmessage = e => {
            this.audioWriter = new AudioBuffer(e.data.sab)
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

            console.log("Test30 : " + this.inputRingBuffer.framesAvailable)

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
            if (this.audioWriter.available_write() >= 1) {
                this.audioWriter.push([meanPitch, meanConfidence, rms])
            }

            // reset variables
            this.accumData = [new Float32Array(this.bufferSize)]
        }
        return true
    }

}


class AudioBuffer {
    private readonly writePointer: Uint32Array
    private readonly readPointer: Uint32Array
    private readonly storage: Float32Array
    private readonly capacityVariable: number
    private readonly sharedArrayBuffer: SharedArrayBuffer

    constructor(sharedArrayBuffer: SharedArrayBuffer) {
        this.sharedArrayBuffer = sharedArrayBuffer

        this.capacityVariable = (sharedArrayBuffer.byteLength - 8) / Float32Array.BYTES_PER_ELEMENT
        this.writePointer = new Uint32Array(this.sharedArrayBuffer, 0, 1)
        this.readPointer = new Uint32Array(this.sharedArrayBuffer, 4, 1)
        this.storage = new Float32Array(this.sharedArrayBuffer, 8, this.capacityVariable)
    }

    push(elements: Array<number>) {
        const rd = Atomics.load(this.readPointer, 0)
        const wr = Atomics.load(this.writePointer, 0)

        if ((wr + 1) % this.capacityVariable == rd) {
            // full
            return 0;
        }

        const to_write = Math.min(this.availableWrite(rd, wr), elements.length)
        const first_part = Math.min(this.capacityVariable - wr, to_write)
        const second_part = to_write - first_part

        this.copy(elements, 0, this.storage, wr, first_part)
        this.copy(elements, first_part, this.storage, 0, second_part)

        // publish the enqueued data to the other side
        Atomics.store(
            this.writePointer,
            0,
            (wr + to_write) % this.capacityVariable
        )

        return to_write
    }


    // Query the free space in the ring buffer. This is the amount of samples that
    // can be queued, with a guarantee of success.
    available_write() {
        const rd = Atomics.load(this.readPointer, 0)
        const wr = Atomics.load(this.writePointer, 0)
        return this.availableWrite(rd, wr)
    }


    private availableWrite(rd, wr) {
        let rv = rd - wr - 1
        if (wr >= rd) {
            rv += this.capacityVariable
        }
        return rv
    }


    // Copy `size` elements from `input`, starting at offset `offset_input`, to
    // `output`, starting at offset `offset_output`.
    private copy(input, offset_input, output, offset_output, size) {
        for (let i = 0; i < size; i++) {
            output[offset_output + i] = input[offset_input + i]
        }
    }
}


registerProcessor("test-audio-processor", TestAudioProcessor)