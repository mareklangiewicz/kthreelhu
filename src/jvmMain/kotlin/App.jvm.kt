package pl.mareklangiewicz.kthreelhu

import androidx.compose.ui.window.*
import pl.mareklangiewicz.kim.*
import pl.mareklangiewicz.kim.Kim.Companion.trigger
import pl.mareklangiewicz.kim.Kim.Companion.toggle
import pl.mareklangiewicz.uwidgets.*

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        Kim.Area {
            'a' trigger { println("a") }
            'b'.toggle()
            'c'.toggle()
            Kim.Frame {
                UText("Kthreelhu Desktop")
            }
        }
    }
}
