package ui

import AppController
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.dom.*

@Composable
fun AppControls(controller: AppController) {
    val state = controller.state

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
            }

            Label(forId = "highestNote") { Text("Highest note: ") }
            Input(type = InputType.Number) {
                id("highestNote")
                value(state.highestNote.toString())
                onInput { event ->
                    val value = event.target.asDynamic().value as String
                    value.toIntOrNull()?.let { state.highestNote = it }
                }
            }
        }

        Button(attrs = {
            onClick { controller.generateSequence() }
        }) {
            Text("Generate sequence")
        }

        Button(attrs = {
            onClick { controller.play() }
        }) {
            Text("Play")
        }

        Button(attrs = {
            onClick { controller.toggleRecording() }
        }) {
            Text(if (state.isRecording) "Stop recording" else "Start recording")
        }

        Button(attrs = {
            onClick { controller.toggleShowTarget() }
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
