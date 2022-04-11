package pl.mareklangiewicz.kthreelhu

import androidx.compose.material3.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import pl.mareklangiewicz.kommon.cmnPlatformIsJs
import pl.mareklangiewicz.kommon.cmnPlatformIsJvm

fun main() = application {
    check(cmnPlatformIsJvm && !cmnPlatformIsJs)
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        Text("Kthreelhu Desktop")
    }
}
