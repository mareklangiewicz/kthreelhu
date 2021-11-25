package pl.mareklangiewicz.kthreelhu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        Text("Kthreelhu Desktop")
    }
}
