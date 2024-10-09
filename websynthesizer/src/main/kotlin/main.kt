package com.kjipo

import com.kjipo.midi.SynthesizerScript
import kotlinx.browser.document
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}


// Note: If the main method is commented in it will run when this module is imported into other modules


//fun main() {
//    val synthesizer = SynthesizerScript()
//    val playButton = document.querySelector("#btnPlay")
//
//    if (playButton != null) {
//        playButton.addEventListener("click", {
//
//            GlobalScope.launch(Dispatchers.Default) {
//                synthesizer.noteOn(60)
//                delay(2000)
//                synthesizer.noteOff(60)
//            }
//        })
//    } else {
//        logger.error { "Play button is null" }
//    }

//}