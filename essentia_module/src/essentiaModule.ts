// const essentia = new Essentia(Module);

/**
 * https://developer.mozilla.org/en-US/docs/Web/API/AudioWorkletProcessor
 */
class TestAudioProcessor implements AudioWorkletProcessor {

    process(inputList, outputList, params) {
        const input = inputList[0]

        console.log("Input: " + input)


        return true
    }


}

// @ts-ignore
registerProcessor("test-audio-processor", TestAudioProcessor)