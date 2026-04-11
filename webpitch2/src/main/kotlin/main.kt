import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level
import org.jetbrains.compose.web.renderComposable
import ui.AppControls
import ui.AppState

fun main() {
    KotlinLoggingConfiguration.logLevel = Level.DEBUG

    val state = AppState()
    val controller = AppController(state)

    renderComposable(rootElementId = "content") {
        AppControls(controller)
    }

    controller.setPitchGraphElement("pitchGraph")
    controller.generateSequence()
}
