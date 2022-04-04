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
export class ChromeLabsRingBuffer {
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
