package ui

import androidx.compose.runtime.*
import kotlinx.browser.localStorage

enum class ConfigMode {
    NORMAL,
    CONFIGURING_LOWER,
    CONFIGURING_UPPER
}

class AppState {
    private var _lowestNote by mutableStateOf(localStorage.getItem("lowestNote")?.toIntOrNull() ?: 60)
    var lowestNote: Int
        get() = _lowestNote
        set(value) {
            _lowestNote = value
            localStorage.setItem("lowestNote", value.toString())
        }

    private var _highestNote by mutableStateOf(localStorage.getItem("highestNote")?.toIntOrNull() ?: 90)
    var highestNote: Int
        get() = _highestNote
        set(value) {
            _highestNote = value
            localStorage.setItem("highestNote", value.toString())
        }

    var isRecording by mutableStateOf(false)
    var currentPitch by mutableStateOf(0.0f)
    var currentCertainty by mutableStateOf(0.0f)
    var configMode by mutableStateOf(ConfigMode.NORMAL)
    var closestMidiNote by mutableStateOf<Int?>(null)
}
