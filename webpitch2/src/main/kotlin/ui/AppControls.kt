package ui

import AppController
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*

@Composable
fun AppControls(controller: AppController) {
    val state = controller.state
    val isNormalMode = state.configMode == ConfigMode.NORMAL

    Div {
        Div {
            Label(forId = "lowestNote") { Text("Lowest note: ") }
            Input(type = InputType.Number) {
                id("lowestNote")
                value(state.lowestNote.toString())
                onInput { event ->
                    val value = event.target.asDynamic().value as String
                    value.toIntOrNull()?.let { state.lowestNote = it }
                }
                if (!isNormalMode) {
                    attr("disabled", "true")
                }
            }

            Button(attrs = {
                onClick {
                    if (state.configMode == ConfigMode.CONFIGURING_LOWER) {
                        controller.setLimit()
                    } else {
                        controller.startConfigureLowerLimit()
                    }
                }
                if (!isNormalMode && state.configMode != ConfigMode.CONFIGURING_LOWER) {
                    attr("disabled", "true")
                }
            }) {
                Text(if (state.configMode == ConfigMode.CONFIGURING_LOWER) "Set as lowest note" else "Configure lower limit")
            }
            if (state.configMode == ConfigMode.CONFIGURING_LOWER) {
                Span {
                    Text(" Closest MIDI note: ${state.closestMidiNote ?: "-"}")
                }
            }

            Label(forId = "highestNote") { Text("Highest note: ") }
            Input(type = InputType.Number) {
                id("highestNote")
                value(state.highestNote.toString())
                onInput { event ->
                    val value = event.target.asDynamic().value as String
                    value.toIntOrNull()?.let { state.highestNote = it }
                }
                if (!isNormalMode) {
                    attr("disabled", "true")
                }
            }

            Button(attrs = {
                onClick {
                    if (state.configMode == ConfigMode.CONFIGURING_UPPER) {
                        controller.setLimit()
                    } else {
                        controller.startConfigureUpperLimit()
                    }
                }
                if (!isNormalMode && state.configMode != ConfigMode.CONFIGURING_UPPER) {
                    attr("disabled", "true")
                }
            }) {
                Text(if (state.configMode == ConfigMode.CONFIGURING_UPPER) "Set as upper note" else "Configure upper limit")
            }
            if (state.configMode == ConfigMode.CONFIGURING_UPPER) {
                Span {
                    Text(" Closest MIDI note: ${state.closestMidiNote ?: "-"}")
                }
            }
        }

        Button(attrs = {
            onClick { controller.generateSequence() }
            if (!isNormalMode) {
                attr("disabled", "true")
            }
        }) {
            Text("Generate sequence")
        }

        Button(attrs = {
            onClick { controller.play() }
            if (!isNormalMode) {
                attr("disabled", "true")
            }
        }) {
            Text("Play")
        }

        Button(attrs = {
            onClick { controller.toggleRecording() }
            if (!isNormalMode) {
                attr("disabled", "true")
            }
        }) {
            Text(if (state.isRecording) "Stop recording" else "Start recording")
        }

        Button(attrs = {
            onClick { controller.toggleShowTarget() }
            if (!isNormalMode) {
                attr("disabled", "true")
            }
        }) {
            Text("Show target")
        }

        Div {
            Text("Pitch: ${state.currentPitch}")
        }
        Div {
            Text("Certainty: ${state.currentCertainty}")
        }
    }
}
