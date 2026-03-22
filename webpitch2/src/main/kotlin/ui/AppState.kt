package ui

import androidx.compose.runtime.*

class AppState {
    var lowestNote by mutableStateOf(40)
    var highestNote by mutableStateOf(60)
    var isRecording by mutableStateOf(false)
    var currentPitch by mutableStateOf(0.0f)
    var currentCertainty by mutableStateOf(0.0f)
}
