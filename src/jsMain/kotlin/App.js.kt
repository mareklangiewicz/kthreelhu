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
import pl.mareklangiewicz.widgets.CmnDText
import pl.mareklangiewicz.widgets.kim.KeyDownEffect
import pl.mareklangiewicz.widgets.kim.Kim
import pl.mareklangiewicz.widgets.kim.Kim.Companion.cmd
import pl.mareklangiewicz.widgets.kim.Kim.Companion.collect
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggledLocally
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
    var camPos by remember { mutableStateOf(XYZ(0.0, 0.0, 20.0)) }
    var camRot by remember { mutableStateOf(XYZ(0.0, 0.0, 0.0)) }

    Key("1").cmd().collect { scope.launch { threeExperiment1() } }
    Key("2").cmd().collect { scope.launch { threeExperiment2() } }

    val toggledY by Key("y").toggledLocally(false)
    val toggledZ by Key("z").toggledLocally(false)
    val toggledR by Key("r").toggledLocally(false)
    val toggledS by Key("s").toggledLocally(false)

    var mousePosBackup: XYZ? = null // null also signifies we are resetting the loop (mmove)
    var camPosBackup: XYZ? = null
    var camRotBackup: XYZ? = null
    Key("c").cmd().collect { camPosBackup = camPos; camRotBackup = camRot; mousePosBackup = null }
    Key("mmove").cmd().collect {
        val evt = it.data as MouseEvent
        val mousePos = evt.screenX.dbl xy evt.screenY.dbl yz 0.0
        val mousePosDelta = mousePosBackup?.let { mousePos - it }
        val factor = if (toggledS) 0.003 else 0.1
        when {
            mousePosBackup == null -> {
                camPosBackup = camPos
                camRotBackup = camRot
                mousePosBackup = mousePos
            }
            toggledY -> camPos = camPosBackup!! + mousePosDelta!! * factor
            toggledZ -> camPos = camPosBackup!! + mousePosDelta!!.run { XYZ(x, 0.0, y) } * factor
            toggledR -> camRot = camRotBackup!! + mousePosDelta!! * factor
            else -> {
                camPos = camPosBackup!!
                camRot = camRotBackup!!
                mousePosBackup = null
            }
        }
    }


    H2 { Text("Kthreelhu JS") }
    Div {
        CmnDText("camPos:$camPos; camRot:$camRot; slow:$toggledS; toggledY:$toggledY; toggledZ:$toggledZ; toggledR:$toggledR", mono = true)
        KthExamples(camPos, camRot)
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
