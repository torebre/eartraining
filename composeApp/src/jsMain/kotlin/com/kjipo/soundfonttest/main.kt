package com.kjipo.soundfonttest

import androidx.compose.ui.ExperimentalComposeUiApi
import com.kjipo.sampleplayer.SamplePlayer
import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.SetupData
import com.kjipo.soundfontparser.loadFile
import com.kjipo.soundfontparser.printChunks
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch


private fun setupUi() {
//    ComposeViewport(document.body!!) {
//        App()
//    }

//    KotlinLoggingConfiguration.LOG_LEVEL = KotlinLoggingLevel.DEBUG

    // Load soundfont data at startup
    GlobalScope.launch {
//        val soundfontData = loadFile("florestan-subset.sf2")
//        val soundfontData = loadFile("soundfonts/r-piano/R-piano.sf2")
        val soundfontData = loadFile("soundfonts/grandpno/Grandpno.sf2")
        val chunks = Parser.parse(soundfontData)
        printChunks(chunks)
        val soundFontData = SetupData.setupData(chunks, soundfontData)

        val samplePlayer = SamplePlayer(soundFontData)
        val sampleNames = samplePlayer.getSampleNames()

        val samplePlayerInteractive = com.kjipo.sampleplayer.SamplePlayerInteractive(soundFontData)

        // Create buttons for each sample
        val body = document.body

        // Add "Play pitch" input and button
        val pitchInput = document.createElement("input")
        pitchInput.setAttribute("type", "number")
        pitchInput.setAttribute("value", "60")
        body?.appendChild(pitchInput)

        val playPitchButton = document.createElement("button")
        playPitchButton.textContent = "Play pitch"
        playPitchButton.addEventListener("click", {
            val pitch = pitchInput.asDynamic().value.toString().toIntOrNull()
            if (pitch != null) {
                console.log("Playing pitch: $pitch")
                samplePlayer.playPitch(pitch)
            } else {
                console.log("Invalid pitch value")
            }
        })
        body?.appendChild(playPitchButton)

        // Add "Play scale" button
        val playScaleButton = document.createElement("button")
        playScaleButton.textContent = "Play scale"
        playScaleButton.addEventListener("click", {
            console.log("Playing scale")
            com.kjipo.sampleplayer.playScale(samplePlayer, 0)
        })
        body?.appendChild(playScaleButton)

        sampleNames.forEachIndexed { index, sampleName ->
            val button = document.createElement("button")
            button.textContent = sampleName
            button.addEventListener("click", {
                console.log("Playing sample: $sampleName")
                samplePlayer.playSample(index)
            })
            body?.appendChild(button)
        }

        val activePitches = mutableSetOf<Int>()

        for (pitch in 60 until 90) {
            val button = document.createElement("button") as org.w3c.dom.HTMLButtonElement
            button.textContent = pitch.toString()
            button.addEventListener("click", {
                if (activePitches.contains(pitch)) {
                    samplePlayerInteractive.pitchOff(pitch)
                    activePitches.remove(pitch)
                    button.style.backgroundColor = ""
                } else {
                    samplePlayerInteractive.pitchOn(pitch)
                    activePitches.add(pitch)
                    button.style.backgroundColor = "green"
                }
            })
            body?.appendChild(button)
        }
    }

    val playButton = document.querySelector("#btnPlay")
    val playNoteButton = document.querySelector("#btnPlayNote")
    val stopPlayNoteButton = document.querySelector("#btnStopPlayNote")
    val testLoadingButton = document.querySelector("#btnTestLoading")
    val playAudioButton = document.querySelector("#btnPlayAudio")

    if (playButton != null) {
        playButton.addEventListener("click", {
            console.log("Play button clicked")

            console.log("Initializing audio context")
            initializeAudioContext()

            GlobalScope.launch {
                console.log("Running setup")
                setup()

                if (playNoteButton != null) {
//                    val postSynthMessage: (Any) -> Unit = js("window.midiFunctions.postSynthMessage")
                    playNoteButton.addEventListener("click", {
//                        postSynthMessage("{type: \"midi\", midi: {type: \"channel\", subtype: \"noteOn\", channel: 0, noteNumber: 60, velocity: 127, tick: 0, track: 1}, delayTime: 0}")
                        js("window.midiFunctions.postSynthMessage({type: \"midi\", midi: {type: \"channel\", subtype: \"noteOn\", channel: 0, noteNumber: 60, velocity: 127, tick: 0, track: 1}, delayTime: 0});")
                    })
                } else {
                    console.log("Play note button is null")
                }
            }
        })
    } else {
        console.log("Play button is null")
    }


    if (stopPlayNoteButton != null) {
        stopPlayNoteButton.addEventListener("click", {
            js("window.midiFunctions.postSynthMessage({type: \"midi\", midi: {type: \"channel\", subtype: \"noteOff\", channel: 0, noteNumber: 60, velocity: 127, tick: 0, track: 1}, delayTime: 0});")
        })
    }

    if (playAudioButton != null) {
        playAudioButton.addEventListener("click", {
            console.log("Play audio button clicked")
            playSimpleAudio()
        })
    }

//    val parser = Parser()
//    testLoadingButton?.addEventListener("click", {
//        GlobalScope.launch {
//            console.log(parser.testLoadFile())
//        }
//    })
}

fun initializeAudioContext() {
    val audioContext = js("new AudioContext();")
    js("window.myAudioContext = audioContext")
}

suspend fun setup() {
    console.log("Starting setup")

//    val fileContents = urlFromFiles(listOf("processor.js"))
//    val addModulePromise: Promise<Any> =
//        js("window.myAudioContext.audioWorklet.addModule(fileContents);")
//
//    addModulePromise.then {
//        console.log("Module added")
//
//        val audioWorkletNode = js("new AudioWorkletNode(window.myAudioContext, 'synth-processor', { numberOfInputs: 0, outputChannelCount: [2] });")
//        audioWorkletNode.connect(js("window.myAudioContext.destination"))
//    }.catch {
//        console.error("Failed to add module", it)
//    }

    console.log("Fetching index.js")

    val response = window.fetch("index.js").await()
    val jsCode = response.text().await()

    console.log("Running index.js")

    // Execute the JavaScript code in global scope
    js("eval(jsCode)")
}

private fun playSimpleAudio() {
    val audioContext: dynamic = js("new (window.AudioContext || window.webkitAudioContext)()")
    val sampleRate = audioContext.sampleRate as Int
    val duration = 1.0
    val frequency = 440.0

    val buffer: dynamic = audioContext.createBuffer(1, (sampleRate * duration).toInt(), sampleRate)
    val channelData: dynamic = buffer.getChannelData(0)

    for (i in 0 until buffer.length as Int) {
        channelData[i] = kotlin.math.sin(2 * kotlin.math.PI * frequency * i / sampleRate)
    }

    val source: dynamic = audioContext.createBufferSource()
    source.buffer = buffer
    source.connect(audioContext.destination)
    source.start()

    console.log("Playing sine wave at $frequency Hz")
}

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    setupUi()
}