package ui

import androidx.compose.runtime.*
import kotlinx.browser.localStorage

enum class ConfigMode {
    NORMAL,
    CONFIGURING_LOWER,
    CONFIGURING_UPPER
}

class AppState {
    private var lowestNoteInternal by mutableStateOf(localStorage.getItem("lowestNote")?.toIntOrNull() ?: 60)
    var lowestNote: Int
        get() = lowestNoteInternal
        set(value) {
            lowestNoteInternal = value
            localStorage.setItem("lowestNote", value.toString())
        }

    private var highestNoteInternal by mutableStateOf(localStorage.getItem("highestNote")?.toIntOrNull() ?: 90)
    var highestNote: Int
        get() = highestNoteInternal
        set(value) {
            highestNoteInternal = value
            localStorage.setItem("highestNote", value.toString())
        }

    private var isDarkModeInternal by mutableStateOf(localStorage.getItem("isDarkMode")?.toBoolean() ?: false)
    var isDarkMode: Boolean
        get() = isDarkModeInternal
        set(value) {
            isDarkModeInternal = value
            localStorage.setItem("isDarkMode", value.toString())
        }

    var isRecording by mutableStateOf(false)
    var isPlayingFirstNote by mutableStateOf(false)
    var currentPitch by mutableStateOf(0.0f)
    var currentCertainty by mutableStateOf(0.0f)
    var configMode by mutableStateOf(ConfigMode.NORMAL)
    var closestMidiNote by mutableStateOf<Int?>(null)
    var canSaveAttempt by mutableStateOf(false)


    fun reset() {
        isRecording = false
        isPlayingFirstNote = false
        currentPitch = 0.0f
        currentCertainty = 0.0f
        configMode = ConfigMode.NORMAL
        closestMidiNote = null
        canSaveAttempt = false
    }

}
