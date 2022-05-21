@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

package webaudioapi

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external object webkitAudioContext {
}

external object webkitOfflineAudioContext {
}

//external enum class ChannelCountMode {
//    'max',
//    `'clamped-max'`,
//    'explicit'
//}

external enum class ChannelInterpretation {
    speakers,
    discrete
}

external enum class PanningModelType {
    equalpower,
    HRTF
}

external enum class DistanceModelType {
    linear,
    inverse,
    exponential
}

external enum class BiquadFilterType {
    lowpass,
    highpass,
    bandpass,
    lowshelf,
    highshelf,
    peaking,
    notch,
    allpass
}

//external enum class OverSampleType {
//    'none',
//    '2x',
//    '4x'
//}

external enum class OscillatorType {
    sine,
    square,
    sawtooth,
    triangle,
    custom
}

external interface AudioContextConstructor