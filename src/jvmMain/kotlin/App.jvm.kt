package pl.mareklangiewicz.kthreelhu

import androidx.compose.ui.window.*
import pl.mareklangiewicz.kommon.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.widgets.kim.*
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggle
import pl.mareklangiewicz.widgets.kim.Kim.Companion.trigger

fun main() = application {
    check(cmnPlatformIsJvm && !cmnPlatformIsJs)
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        Kim.Area {
            'a' trigger { println("a") }
            'b'.toggle()
            'c'.toggle()
            Kim.Frame {
                UBoxedText("Kthreelhu Desktop")
            }
        }
    }
}
