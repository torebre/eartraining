import {EssentiaWASM} from './essentia/essentia-wasm.module.js'
import {Essentia} from './essentia/essentia.js-core.js'

// import { RingBuffer } from "./essentia/wasm-audio-helper.js";

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
    private audio_writer: AudioWriter

    constructor(options) {
        super()

        this.bufferSize = options.processorOptions.bufferSize
        this.sampleRate = options.processorOptions.sampleRate
        this.essentia = new Essentia(EssentiaWASM)
        this.frameSize = this.bufferSize / 2
        this.hopSize = this.frameSize / 4


        // buffersize mismatch helpers
        this.inputRingBuffer = new ChromeLabsRingBuffer(this.bufferSize, this.channelCount);

        this.accumData = [new Float32Array(this.bufferSize)];

        // SAB config
        this.port.onmessage = e => {
            this.audio_writer = new AudioWriter(new RingBuffer(e.data.sab, Float32Array));
        };
    }

    process(inputList, outputList, params) {
        const input = inputList[0];
        // let output = outputList[0];

        // console.log("Input: " + input)

        this.inputRingBuffer.push(input);

        // console.log("Test26: " +this.inputRingBuffer.framesAvailable)

        if (this.inputRingBuffer.framesAvailable >= this.bufferSize) {

            console.log("Test24: " +this.inputRingBuffer.framesAvailable)

            this.inputRingBuffer.pull(this.accumData);

            console.log("Test30 : " +this.inputRingBuffer.framesAvailable)
            // console.log("Test31: " +this.accumData[0])

            const accumDataVector = this.essentia.arrayToVector(this.accumData[0]);

            // console.log("Test25: " +JSON.stringify(accumDataVector))

            const rms = this.essentia.RMS(accumDataVector).rms;

            // const algoOutput = this._essentia.PitchYinProbabilistic(essentia.arrayToVector(this._accumData[0]), this._frameSize, this._hopSize, this._rmsThreshold, "zero", false, this._sampleRate);
            const algoOutput = this.essentia.PitchMelodia(
                accumDataVector,
                10, 3, this.frameSize, false, 0.8, this.hopSize, 1, 40, this.highestFreq, 100, this.lowestFreq, 20, 0.9, 0.9, 27.5625, this.lowestFreq, this.sampleRate, 100
            );

            console.log("Test35: " +JSON.stringify(algoOutput.pitch))

            const pitchFrames = this.essentia.vectorToArray(algoOutput.pitch);
            const confidenceFrames = this.essentia.vectorToArray(algoOutput.pitchConfidence);

            // average frame-wise pitches in pitch before writing to SAB
            const numVoicedFrames = pitchFrames.filter(p => p > 0).length;
            // const numFrames = pitchFrames.length;
            const meanPitch = pitchFrames.reduce((acc, val) => acc + val, 0) / numVoicedFrames;
            const meanConfidence = confidenceFrames.reduce((acc, val) => acc + val, 0) / numVoicedFrames;
            console.info("audio: ", meanPitch, meanConfidence, rms);
            // write to SAB using AudioWriter object so that pitch output can be accesed from the main UI thread
            if (this.audio_writer.available_write() >= 1) {
                this.audio_writer.enqueue([meanPitch, meanConfidence, rms]);
            }

            // reset variables
            this.accumData = [new Float32Array(this.bufferSize)];
        }


        // console.log("Output: " +output[0]);
        return true;
    }


}


// helper classes from https://github.com/GoogleChromeLabs/web-audio-samples/blob/gh-pages/audio-worklet/design-pattern/lib/wasm-audio-helper.js#L170:

/**
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

// Basic byte unit of WASM heap. (16 bit = 2 bytes)
const BYTES_PER_UNIT = Uint16Array.BYTES_PER_ELEMENT;

// Byte per audio sample. (32 bit float)
const BYTES_PER_SAMPLE = Float32Array.BYTES_PER_ELEMENT;

// The max audio channel on Chrome is 32.
const MAX_CHANNEL_COUNT = 32;

// WebAudio's render quantum size.
const RENDER_QUANTUM_FRAMES = 128;

/**
 * A JS FIFO implementation for the AudioWorklet. 3 assumptions for the
 * simpler operation:
 *  1. the push and the pull operation are done by 128 frames. (Web Audio
 *    API's render quantum size in the speficiation)
 *  2. the channel count of input/output cannot be changed dynamically.
 *    The AudioWorkletNode should be configured with the `.channelCount = k`
 *    (where k is the channel count you want) and
 *    `.channelCountMode = explicit`.
 *  3. This is for the single-thread operation. (obviously)
 *
 * @class
 */
class ChromeLabsRingBuffer {
    private readIndex = 0
    private writeIndex = 0
    private framesAvailableVariable = 0
    private length: number
    private channelCount: number
    private channelData = []

    /**
     * @constructor
     * @param  {number} length Buffer length in frames.
     * @param  {number} channelCount Buffer channel count.
     */
    constructor(length, channelCount) {
        this.channelCount = channelCount;
        this.length = length;
        for (let i = 0; i < this.channelCount; ++i) {
            this.channelData[i] = new Float32Array(length);
        }
    }

    /**
     * Getter for Available frames in buffer.
     *
     * @return {number} Available frames in buffer.
     */
    get framesAvailable() {
        return this.framesAvailableVariable;
    }

    /**
     * Push a sequence of Float32Arrays to buffer.
     *
     * @param  {array} arraySequence A sequence of Float32Arrays.
     */
    push(arraySequence) {
        // The channel count of arraySequence and the length of each channel must
        // match with this buffer obejct.

        // Transfer data from the |arraySequence| storage to the internal buffer.
        let sourceLength = arraySequence[0].length;
        for (let i = 0; i < sourceLength; ++i) {
            let writeIndex = (this.writeIndex + i) % this.length;
            for (let channel = 0; channel < this.channelCount; ++channel) {
                this.channelData[channel][writeIndex] = arraySequence[channel][i];
            }
        }

        this.writeIndex += sourceLength;
        if (this.writeIndex >= this.length) {
            this.writeIndex = 0;
        }

        // For excessive frames, the buffer will be overwritten.
        this.framesAvailableVariable += sourceLength;
        if (this.framesAvailableVariable > this.length) {
            this.framesAvailableVariable = this.length;
        }
    }

    /**
     * Pull data out of buffer and fill a given sequence of Float32Arrays.
     *
     * @param  {array} arraySequence An array of Float32Arrays.
     */
    pull(arraySequence) {
        // The channel count of arraySequence and the length of each channel must
        // match with this buffer obejct.

        // If the FIFO is completely empty, do nothing.
        if (this.framesAvailableVariable === 0) {
            return;
        }

        let destinationLength = arraySequence[0].length;

        // Transfer data from the internal buffer to the |arraySequence| storage.
        for (let i = 0; i < destinationLength; ++i) {
            let readIndex = (this.readIndex + i) % this.length;
            for (let channel = 0; channel < this.channelCount; ++channel) {
                arraySequence[channel][i] = this.channelData[channel][readIndex];
            }
        }

        this.readIndex += destinationLength;
        if (this.readIndex >= this.length) {
            this.readIndex = 0;
        }

        this.framesAvailableVariable -= destinationLength;
        if (this.framesAvailableVariable < 0) {
            this.framesAvailableVariable = 0;
        }
    }
} // class ChromeLabsRingBuffer

// A Single Producer - Single Consumer thread-safe wait-free ring buffer.
//
// The producer and the consumer can be separate thread, but cannot change role,
// except with external synchronization.

class RingBuffer {
    private _type: any;
    private buf: any;
    private write_ptr: Uint32Array;
    private read_ptr: Uint32Array
    private storage: any
    private capacityVariable: number

    static getStorageForCapacity(capacity, type) {
        if (!type.BYTES_PER_ELEMENT) {
            throw "Pass in a ArrayBuffer subclass";
        }
        var bytes = 8 + (capacity + 1) * type.BYTES_PER_ELEMENT;
        return new SharedArrayBuffer(bytes);
    }

    // `sab` is a SharedArrayBuffer with a capacity calculated by calling
    // `getStorageForCapacity` with the desired capacity.
    constructor(sab, type) {
        // if (!ArrayBuffer.__proto__.isPrototypeOf(type) &&
        //     type.BYTES_PER_ELEMENT !== undefined) {
        //     throw "Pass a concrete typed array class as second argument";
        // }

        // Maximum usable size is 1<<32 - type.BYTES_PER_ELEMENT bytes in the ring
        // buffer for this version, easily changeable.
        // -4 for the write ptr (uint32_t offsets)
        // -4 for the read ptr (uint32_t offsets)
        // capacity counts the empty slot to distinguish between full and empty.
        this._type = type;
        this.capacityVariable = (sab.byteLength - 8) / type.BYTES_PER_ELEMENT;
        this.buf = sab;
        this.write_ptr = new Uint32Array(this.buf, 0, 1);
        this.read_ptr = new Uint32Array(this.buf, 4, 1);
        this.storage = new type(this.buf, 8, this.capacity);
    }

    // Returns the type of the underlying ArrayBuffer for this RingBuffer. This
    // allows implementing crude type checking.
    type() {
        return this._type.name;
    }

    // Push bytes to the ring buffer. `bytes` is an typed array of the same type
    // as passed in the ctor, to be written to the queue.
    // Returns the number of elements written to the queue.
    push(elements) {
        var rd = Atomics.load(this.read_ptr, 0);
        var wr = Atomics.load(this.write_ptr, 0);

        if ((wr + 1) % this._storage_capacity() == rd) {
            // full
            return 0;
        }

        let to_write = Math.min(this._available_write(rd, wr), elements.length);
        let first_part = Math.min(this._storage_capacity() - wr, to_write);
        let second_part = to_write - first_part;

        this._copy(elements, 0, this.storage, wr, first_part);
        this._copy(elements, first_part, this.storage, 0, second_part);

        // publish the enqueued data to the other side
        Atomics.store(
            this.write_ptr,
            0,
            (wr + to_write) % this._storage_capacity()
        );

        return to_write;
    }

    // Read `elements.length` elements from the ring buffer. `elements` is a typed
    // array of the same type as passed in the ctor.
    // Returns the number of elements read from the queue, they are placed at the
    // beginning of the array passed as parameter.
    pop(elements) {
        var rd = Atomics.load(this.read_ptr, 0);
        var wr = Atomics.load(this.write_ptr, 0);

        if (wr == rd) {
            return 0;
        }

        let to_read = Math.min(this._available_read(rd, wr), elements.length);

        let first_part = Math.min(this._storage_capacity() - rd, elements.length);
        let second_part = to_read - first_part;

        this._copy(this.storage, rd, elements, 0, first_part);
        this._copy(this.storage, 0, elements, first_part, second_part);

        Atomics.store(this.read_ptr, 0, (rd + to_read) % this._storage_capacity());

        return to_read;
    }

    // True if the ring buffer is empty false otherwise. This can be late on the
    // reader side: it can return true even if something has just been pushed.
    empty() {
        var rd = Atomics.load(this.read_ptr, 0);
        var wr = Atomics.load(this.write_ptr, 0);

        return wr == rd;
    }

    // True if the ring buffer is full, false otherwise. This can be late on the
    // write side: it can return true when something has just been poped.
    full() {
        var rd = Atomics.load(this.read_ptr, 0);
        var wr = Atomics.load(this.write_ptr, 0);

        return (wr + 1) % this.capacityVariable != rd;
    }

    // The usable capacity for the ring buffer: the number of elements that can be
    // stored.
    capacity() {
        return this.capacityVariable - 1;
    }

    // Number of elements available for reading. This can be late, and report less
    // elements that is actually in the queue, when something has just been
    // enqueued.
    available_read() {
        var rd = Atomics.load(this.read_ptr, 0);
        var wr = Atomics.load(this.write_ptr, 0);
        return this._available_read(rd, wr);
    }

    // Number of elements available for writing. This can be late, and report less
    // elemtns that is actually available for writing, when something has just
    // been dequeued.
    available_write() {
        var rd = Atomics.load(this.read_ptr, 0);
        var wr = Atomics.load(this.write_ptr, 0);
        return this._available_write(rd, wr);
    }

    // private methods //

    // Number of elements available for reading, given a read and write pointer..
    _available_read(rd, wr) {
        if (wr > rd) {
            return wr - rd;
        } else {
            return wr + this._storage_capacity() - rd;
        }
    }

    // Number of elements available from writing, given a read and write pointer.
    _available_write(rd, wr) {
        let rv = rd - wr - 1;
        if (wr >= rd) {
            rv += this._storage_capacity();
        }
        return rv;
    }

    // The size of the storage for elements not accounting the space for the index.
    _storage_capacity(): number {
        return this.capacityVariable;
    }

    // Copy `size` elements from `input`, starting at offset `offset_input`, to
    // `output`, starting at offset `offset_output`.
    _copy(input, offset_input, output, offset_output, size) {
        for (var i = 0; i < size; i++) {
            output[offset_output + i] = input[offset_input + i];
        }
    }
}


class AudioWriter {
    public ringbuf: any

    // From a RingBuffer, build an object that can enqueue enqueue audio in a ring
    // buffer.
    constructor(ringbuf) {
        if (ringbuf.type() != "Float32Array") {
            throw "This class requires a ring buffer of Float32Array";
        }
        this.ringbuf = ringbuf;
    }

    // Enqueue a buffer of interleaved audio into the ring buffer.
    // Returns the number of samples that have been successfuly written to the
    // queue. `buf` is not written to during this call, so the samples that
    // haven't been written to the queue are still available.
    enqueue(buf) {
        return this.ringbuf.push(buf);
    }

    // Query the free space in the ring buffer. This is the amount of samples that
    // can be queued, with a guarantee of success.
    available_write() {
        return this.ringbuf.available_write();
    }
}


registerProcessor("test-audio-processor", TestAudioProcessor)