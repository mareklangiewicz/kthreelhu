@file:OptIn(ExperimentalComposeWebWidgetsApi::class)
package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.ui.Styles
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import pl.mareklangiewicz.widgets.CmnDText
import pl.mareklangiewicz.widgets.kim.KeyDownEffect
import pl.mareklangiewicz.widgets.kim.Kim
import pl.mareklangiewicz.widgets.kim.Kim.Companion.cmd
import pl.mareklangiewicz.widgets.kim.Kim.Companion.collect
import pl.mareklangiewicz.widgets.kim.Kim.Companion.state
import pl.mareklangiewicz.widgets.kim.Kim.Key
import pl.mareklangiewicz.widgets.kim.MouseMoveEffect
import pl.mareklangiewicz.widgets.kim.MouseWheelEffect

fun main() {
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    val root = document.getElementById("root") as HTMLElement
    renderComposable(root = root) { AppJs() }
}

@Composable fun AppJs() {
    Style(Styles)
    Kim.Area {
        Kim.KeyDownEffect(window)
        Kim.MouseMoveEffect(window)
        Kim.MouseWheelEffect(window)
        Key("q").cmd().collect { window.close() }
        Kim.Frame { AppContent() }
    }
}

@Composable private fun AppContent() {
    val scope = rememberCoroutineScope()
    var camPosX by remember { mutableStateOf(0.0) }
    var camPosY by remember { mutableStateOf(0.0) }
    var camPosZ by remember { mutableStateOf(10.0) }

    Key("1").cmd().collect { scope.launch { threeExperiment1() } }
    Key("2").cmd().collect { scope.launch { threeExperiment2() } }
    Key("h").cmd().collect { camPosX += 0.1 }
    Key("l").cmd().collect { camPosX -= 0.1 }
    Key("k").cmd().collect { camPosY -= 0.1 }
    Key("j").cmd().collect { camPosY += 0.1 }
    Key("i").cmd().collect { camPosZ -= 0.1 }
    Key("o").cmd().collect { camPosZ += 0.1 }
    Key("mousemove").cmd().collect {
        val evt = it.data as MouseEvent
        if (evt.buttons.toInt() != 0) {
            camPosX = evt.offsetX / 100 + 5
            camPosY = evt.offsetY / 100 + 5
        }
    }
    Key("wheel").cmd().collect {
        camPosZ += (it.data as WheelEvent).deltaY.toFloat() / 100
    }

    val toggled by Key("t").cmd(toggled = false).state { it?.toggled ?: false }

    H2 { Text("Kthreelhu JS ($toggled)") }
    Div {
        CmnDText("camera: ${camPosX.toFixed()}, ${camPosY.toFixed()}, ${camPosZ.toFixed()}", mono = true)
        CmnDText("try mouse click+move and wheel", mono = true)
        KthExamples(camPosX xy camPosY yz camPosZ)
    }
}
fun threeExperiment1() {
    console.log("TODO")
}

suspend fun threeExperiment2() {
    console.log("TODO")
//    val system = parseJsonBraxSystem("./walking_ant.json")
//    model?.scene?.addBraxSystem(system)
//    // TODO: trajectory/animation stuff (based on brax repo)
//    // TODO: review all brax stuff after something is moving..
}
