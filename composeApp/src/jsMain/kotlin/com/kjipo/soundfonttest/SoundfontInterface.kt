package com.kjipo.soundfonttest

import kotlin.js.Promise

//@JsModule("soundfont2")
//@JsNonModule
//external val Soundfont2: (buffer: dynamic) -> dynamic

@JsModule("soundfont2")
@JsNonModule
external val Soundfont: (context: dynamic, instrumentName: String, options: SoundfontOptions?) -> Promise<Instrument>

external interface SoundfontOptions {
    var format: String? // 'mp3' | 'ogg'
    var soundfont: String? // 'MusyngKite' | 'FluidR3_GM'
    var nameToUrl: ((name: String, soundfont: String, format: String) -> String)?
    var gain: Number?
    var release: Number?
    var attack: Number?
    var decay: Number?
    var sustain: Number?
}

external interface Instrument {
    fun play(note: String, time: Number = definedExternally, options: NoteOptions = definedExternally): Promise<dynamic>
    fun stop(time: Number = definedExternally)
    fun connect(destination: dynamic)
    fun disconnect()
}

external interface NoteOptions {
    var gain: Number?
    var duration: Number?
    var attack: Number?
    var decay: Number?
    var sustain: Number?
    var release: Number?
    var adsr: Array<Number>?
}
