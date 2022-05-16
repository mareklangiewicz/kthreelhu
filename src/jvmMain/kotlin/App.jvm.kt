package pl.mareklangiewicz.kthreelhu

import androidx.compose.material3.*
import androidx.compose.ui.window.*
import org.jetbrains.compose.common.ui.*
import pl.mareklangiewicz.kommon.*
import pl.mareklangiewicz.widgets.kim.*
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggle
import pl.mareklangiewicz.widgets.kim.Kim.Companion.trigger

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() = application {
    check(cmnPlatformIsJvm && !cmnPlatformIsJs)
    Window(onCloseRequest = ::exitApplication, title = "Kthreelhu") {
        Kim.Area {
            'a' trigger { println("a") }
            'b'.toggle()
            'c'.toggle()
            Kim.Frame {
                Text("Kthreelhu Desktop")
            }
        }
    }
}
