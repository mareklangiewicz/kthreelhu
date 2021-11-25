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
import pl.mareklangiewicz.widgets.CmnDText
import pl.mareklangiewicz.widgets.kim.Kim
import pl.mareklangiewicz.widgets.kim.KimKeyDownEffect

fun main() {
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    val root = document.getElementById("root") as HTMLElement
    renderComposable(root = root) { AppJs() }
}

@Composable fun AppJs() {
    Style(Styles)
    Kim.Area {
        val kim = Kim.kim
        KimKeyDownEffect(kim, window)
        kim.Cmd("q") { window.close() }
        Kim.Frame { AppContent() }
    }
}

@Composable private fun AppContent() {
    val scope = rememberCoroutineScope()
    var camPosX by remember { mutableStateOf(0.0) }
    var camPosY by remember { mutableStateOf(0.0) }
    var camPosZ by remember { mutableStateOf(10.0) }

    val kim = Kim.kim
    kim.Cmd("1") { scope.launch { threeExperiment1() } }
    kim.Cmd("2") { scope.launch { threeExperiment2() } }
    kim.Cmd("h") { camPosX += 0.1 }
    kim.Cmd("l") { camPosX -= 0.1 }
    kim.Cmd("k") { camPosY -= 0.1 }
    kim.Cmd("j") { camPosY += 0.1 }
    kim.Cmd("i") { camPosZ -= 0.1 }
    kim.Cmd("o") { camPosZ += 0.1 }

    H2 { Text("Kthreelhu JS") }
    Div(attrs = {
        onWheel {
            it.preventDefault()
            camPosZ += it.deltaY.toFloat() / 100
        }
        onMouseMove {
            if (it.buttons.toInt() != 0) {
                camPosX = - it.offsetX / 100 + 5
                camPosY = it.offsetY / 100 - 5
            }
        }
    }) {
        CmnDText("try mouse click+move and wheel", mono = true)
        KthExample1(camPosX, camPosY, camPosZ)
        CmnDText("camera: ${camPosX.toFixed()}, ${camPosY.toFixed()}, ${camPosZ.toFixed()}", mono = true)
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
