package pl.mareklangiewicz.kthreelhu

import androidx.compose.ui.window.*
import pl.mareklangiewicz.uwidgets.*

// Three.js is browser-only, so the desktop app is just a placeholder window for now.
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        UWidgetsAwt {
            UText("Kthreelhu Desktop")
        }
    }
}
