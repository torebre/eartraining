package com.kjipo.soundfonttest

import androidx.compose.ui.ExperimentalComposeUiApi
import com.kjipo.sampleplayer.SamplePlayer
import com.kjipo.soundfontparser.Parser
import com.kjipo.soundfontparser.SetupData
import com.kjipo.soundfontparser.loadFile
import com.kjipo.soundfontparser.printChunks
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
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
}


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    setupUi()
}