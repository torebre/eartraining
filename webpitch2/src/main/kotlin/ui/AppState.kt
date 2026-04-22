package ui

import androidx.compose.runtime.*

enum class ConfigMode {
    NORMAL,
    CONFIGURING_LOWER,
    CONFIGURING_UPPER
}

class AppState {
    var lowestNote by mutableStateOf(60)
    var highestNote by mutableStateOf(90)
    var isRecording by mutableStateOf(false)
    var currentPitch by mutableStateOf(0.0f)
    var currentCertainty by mutableStateOf(0.0f)
    var configMode by mutableStateOf(ConfigMode.NORMAL)
    var closestMidiNote by mutableStateOf<Int?>(null)
}
