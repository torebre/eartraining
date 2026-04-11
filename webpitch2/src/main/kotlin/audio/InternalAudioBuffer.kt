package audio

import org.khronos.webgl.*
import kotlin.math.min

/**
 * A ring buffer implementation using SharedArrayBuffer and Atomics for concurrent access.
 * Matches the memory layout and behavior of the original internalAudioBuffer.js.
 *
 * Layout:
 * [0..3]: writePointer (Uint32, 1 element)
 * [4..7]: readPointer (Uint32, 1 element)
 * [8..]: storage (Float32, capacityVariable elements)
 */
class InternalAudioBuffer(private val sharedArrayBuffer: ArrayBuffer) {
    private val capacityVariable: Int = (sharedArrayBuffer.byteLength - 8) / Float32Array.BYTES_PER_ELEMENT
    private val writePointer = Uint32Array(sharedArrayBuffer.asDynamic(), 0, 1)
    private val readPointer = Uint32Array(sharedArrayBuffer.asDynamic(), 4, 1)
    private val storage = Float32Array(sharedArrayBuffer.asDynamic(), 8, capacityVariable)

    fun push(elements: Float32Array): Int {
        val rd = Atomics.load(readPointer, 0)
        val wr = Atomics.load(writePointer, 0)
        
        if ((wr + 1) % capacityVariable == rd) {
            // full
            return 0
        }
        
        val toWrite = min(availableWriteInternal(rd, wr), elements.length)
        val firstPart = min(capacityVariable - wr, toWrite)
        val secondPart = toWrite - firstPart
        
        copy(elements, 0, storage, wr, firstPart)
        if (secondPart > 0) {
            copy(elements, firstPart, storage, 0, secondPart)
        }
        
        // publish the enqueued data to the other side
        Atomics.store(writePointer, 0, (wr + toWrite) % capacityVariable)
        return toWrite
    }

    fun availableWrite(): Int {
        val rd = Atomics.load(readPointer, 0)
        val wr = Atomics.load(writePointer, 0)
        return availableWriteInternal(rd, wr)
    }

    private fun availableWriteInternal(rd: Int, wr: Int): Int {
        var rv = rd - wr - 1
        if (wr >= rd) {
            rv += capacityVariable
        }
        return rv
    }

    fun availableRead(): Int {
        val rd = Atomics.load(readPointer, 0)
        val wr = Atomics.load(writePointer, 0)
        return availableReadInternal(rd, wr)
    }

    private fun availableReadInternal(rd: Int, wr: Int): Int {
        return if (wr > rd) {
            wr - rd
        } else if (wr < rd) {
            wr + capacityVariable - rd
        } else {
            0
        }
    }

    fun dequeue(buf: Float32Array): Int {
        if (empty()) {
            return 0
        }
        return pop(buf)
    }

    fun empty(): Boolean {
        val rd = Atomics.load(readPointer, 0)
        val wr = Atomics.load(writePointer, 0)
        return wr == rd
    }

    private fun pop(elements: Float32Array): Int {
        val rd = Atomics.load(readPointer, 0)
        val wr = Atomics.load(writePointer, 0)
        if (wr == rd) {
            return 0
        }
        
        val toRead = min(availableReadInternal(rd, wr), elements.length)
        val firstPart = min(capacityVariable - rd, toRead)
        val secondPart = toRead - firstPart
        
        copy(storage, rd, elements, 0, firstPart)
        if (secondPart > 0) {
            copy(storage, 0, elements, firstPart, secondPart)
        }
        
        Atomics.store(readPointer, 0, (rd + toRead) % capacityVariable)
        return toRead
    }

    private fun copy(input: Float32Array, offsetInput: Int, output: Float32Array, offsetOutput: Int, size: Int) {
        val inputDyn = input.asDynamic()
        val outputDyn = output.asDynamic()
        for (i in 0 until size) {
            outputDyn[offsetOutput + i] = inputDyn[offsetInput + i]
        }
    }
}

// Minimal external declaration for Atomics since it's not always in the default stdlib for Kotlin/JS
private external object Atomics {
    fun load(typedArray: Uint32Array, index: Int): Int
    fun store(typedArray: Uint32Array, index: Int, value: Int): Int
}
