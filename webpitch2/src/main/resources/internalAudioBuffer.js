class InternalAudioBuffer {
    writePointer;
    readPointer;
    storage;
    capacityVariable;
    sharedArrayBuffer;

    constructor(sharedArrayBuffer) {
        this.sharedArrayBuffer = sharedArrayBuffer;
        this.capacityVariable = (sharedArrayBuffer.byteLength - 8) / Float32Array.BYTES_PER_ELEMENT;
        this.writePointer = new Uint32Array(this.sharedArrayBuffer, 0, 1);
        this.readPointer = new Uint32Array(this.sharedArrayBuffer, 4, 1);
        this.storage = new Float32Array(this.sharedArrayBuffer, 8, this.capacityVariable);
    }

    push(elements) {
        const rd = Atomics.load(this.readPointer, 0);
        const wr = Atomics.load(this.writePointer, 0);
        if ((wr + 1) % this.capacityVariable == rd) {
            // full
            return 0;
        }
        const to_write = Math.min(this.availableWriteInternal(rd, wr), elements.length);
        const first_part = Math.min(this.capacityVariable - wr, to_write);
        const second_part = to_write - first_part;
        this.copy(elements, 0, this.storage, wr, first_part);
        this.copy(elements, first_part, this.storage, 0, second_part);
        // publish the enqueued data to the other side
        Atomics.store(this.writePointer, 0, (wr + to_write) % this.capacityVariable);
        return to_write;
    }

    // Query the free space in the ring buffer. This is the amount of samples that
    // can be queued, with a guarantee of success.
    availableWrite() {
        const rd = Atomics.load(this.readPointer, 0);
        const wr = Atomics.load(this.writePointer, 0);
        return this.availableWriteInternal(rd, wr);
    }

    availableWriteInternal(rd, wr) {
        let rv = rd - wr - 1;
        if (wr >= rd) {
            rv += this.capacityVariable;
        }
        return rv;
    }

    availableRead() {
        const rd = Atomics.load(this.readPointer, 0);
        const wr = Atomics.load(this.writePointer, 0);
        return this.availableReadInternal(rd, wr);
    }

    // Number of elements available for reading, given a read and write pointer
    availableReadInternal(rd, wr) {
        if (wr > rd) {
            return wr - rd;
        } else {
            return wr + this.capacityVariable - rd;
        }
    }

    // Attempt to dequeue at most `buf.length` samples from the queue. This
    // returns the number of samples dequeued. If greater than 0, the samples are
    // at the beginning of `buf`
    dequeue(buf) {
        if (this.empty()) {
            return 0;
        }
        return this.pop(buf);
    }

    // True if the ring buffer is empty false otherwise. This can be late on the
    // reader side: it can return true even if something has just been pushed.
    empty() {
        var rd = Atomics.load(this.readPointer, 0);
        var wr = Atomics.load(this.writePointer, 0);
        return wr == rd;
    }

    // Read `elements.length` elements from the ring buffer. `elements` is a typed
    // array of the same type as passed in the ctor.
    // Returns the number of elements read from the queue, they are placed at the
    // beginning of the array passed as parameter.
    pop(elements) {
        var rd = Atomics.load(this.readPointer, 0);
        var wr = Atomics.load(this.writePointer, 0);
        if (wr == rd) {
            return 0;
        }
        let to_read = Math.min(this.availableReadInternal(rd, wr), elements.length);
        let first_part = Math.min(this.capacityVariable - rd, elements.length);
        let second_part = to_read - first_part;
        this.copy(this.storage, rd, elements, 0, first_part);
        this.copy(this.storage, 0, elements, first_part, second_part);
        Atomics.store(this.readPointer, 0, (rd + to_read) % this.capacityVariable);
        return to_read;
    }

    // Copy `size` elements from `input`, starting at offset `offset_input`, to
    // `output`, starting at offset `offset_output`.
    copy(input, offset_input, output, offset_output, size) {
        for (let i = 0; i < size; i++) {
            output[offset_output + i] = input[offset_input + i];
        }
    }
}
