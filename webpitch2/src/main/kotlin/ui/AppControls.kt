package ui

import AppController
import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*

@Composable
fun AppControls(controller: AppController) {
    val state = controller.state
    val isNormalMode = state.configMode == ConfigMode.NORMAL

    Div(attrs = {
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            if (state.isDarkMode) {
                backgroundColor(Color("black"))
                color(Color("white"))
            } else {
                backgroundColor(Color("white"))
                color(Color("black"))
            }
            minHeight(100.vh)
            padding(10.px)
            boxSizing("border-box")
        }
    }) {
        // Dark mode toggle button in top right
        Div(attrs = {
            style {
                position(Position.Absolute)
                top(10.px)
                right(10.px)
            }
        }) {
            Button(attrs = {
                onClick { state.isDarkMode = !state.isDarkMode }
            }) {
                Text(if (state.isDarkMode) "Light Mode" else "Dark Mode")
            }
        }

        // Row 1: Action buttons
        Div(attrs = { style { marginBottom(10.px) } }) {
            val buttonStyle: AttrBuilderContext<*> = {
                style {
                    marginRight(10.px)
                }
            }

            Button(attrs = {
                buttonStyle()
                onClick { controller.generateSequence() }
                if (!isNormalMode) {
                    attr("disabled", "true")
                }
            }) {
                Text("Generate sequence")
            }

            Button(attrs = {
                buttonStyle()
                onClick { controller.play() }
                if (!isNormalMode) {
                    attr("disabled", "true")
                }
            }) {
                Text("Play")
            }

            Button(attrs = {
                buttonStyle()
                onClick { controller.toggleRecording() }
                if (!isNormalMode) {
                    attr("disabled", "true")
                }
            }) {
                Text(if (state.isRecording) "Stop recording" else "Start recording")
            }

            Button(attrs = {
                buttonStyle()
                onClick { controller.toggleShowTarget() }
                if (!isNormalMode) {
                    attr("disabled", "true")
                }
            }) {
                Text("Show target")
            }
        }

        // Row 2: Pitch and certainty
        Div(attrs = { style { marginBottom(10.px) } }) {
            Span(attrs = { style { marginRight(20.px) } }) {
                Text("Pitch: ${state.currentPitch.asDynamic().toFixed(2)}")
            }
            Span {
                Text("Certainty: ${state.currentCertainty.asDynamic().toFixed(2)}")
            }
        }

        // Row 3: Lowest note configuration
        Div(attrs = { style { marginBottom(10.px) } }) {
            Label(forId = "lowestNote", attrs = { style { marginRight(5.px) } }) { Text("Lowest note: ") }
            Input(type = InputType.Number) {
                id("lowestNote")
                style {
                    width(50.px)
                    marginRight(10.px)
                }
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
                style { marginRight(10.px) }
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
        }

        // Row 4: Highest note configuration
        Div(attrs = { style { marginBottom(10.px) } }) {
            Label(forId = "highestNote", attrs = { style { marginRight(5.px) } }) { Text("Highest note: ") }
            Input(type = InputType.Number) {
                id("highestNote")
                style {
                    width(50.px)
                    marginRight(10.px)
                }
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
                style { marginRight(10.px) }
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
    }
}
