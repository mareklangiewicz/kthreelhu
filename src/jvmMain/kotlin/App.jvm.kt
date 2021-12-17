package pl.mareklangiewicz.kthreelhu

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import pl.mareklangiewicz.kommon.cmnPlatformIsJs
import pl.mareklangiewicz.kommon.cmnPlatformIsJvm

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() = application {
    check(cmnPlatformIsJvm && !cmnPlatformIsJs)
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        Text("Kthreelhu Desktop")
    }
}
