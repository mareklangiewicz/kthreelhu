package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.*
import kotlinx.coroutines.*
import org.jetbrains.compose.web.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.ui.*
import org.w3c.dom.*
import pl.mareklangiewicz.kommon.*
import pl.mareklangiewicz.umath.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.widgets.kim.*
import pl.mareklangiewicz.widgets.kim.Kim.Companion.cmdMouseMove
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggle
import pl.mareklangiewicz.widgets.kim.Kim.Companion.trigger

fun main() {
    check(!cmnPlatformIsJvm && cmnPlatformIsJs)
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    tryToInstallAppIn(document.getElementById("rootForAppJs"))
}

fun tryToInstallAppIn(rootElement: Element?) {
    when (rootElement as? HTMLElement) {
        null -> console.warn("Kthreelhu: Incorrect rootElement")
        else -> renderComposable(root = rootElement) { AppJs() }
    }
}

@Composable fun AppJs() {
    Style(Styles)
    Kim.Area {
        Kim.KeyDownEffect(window)
        Kim.MouseMoveEffect(window)
        Kim.MouseWheelEffect(window)
        Kim.GamepadEffect(window)
        'q' trigger { window.close() }
        Kim.Frame { AppContent() }
    }
}

@Composable private fun AppContent() {
    val scope = rememberCoroutineScope()
    var camPos: XYZ by remember { mutableStateOf(XYZ(0.0, 0.0, 20.0)) }
    var camRot: XYZ by remember { mutableStateOf(XYZ(0.0, 0.0, 0.0)) }

    'f' trigger {
        scope.launch {
            if (document.fullscreenElement == null)
                document.getElementsByTagName("canvas")[0]!!.requestFullscreen()
            else
                document.exitFullscreen()
        }
    }
    '9' trigger { scope.launch { threeExperiment9() } }

    val te by 'e'.toggle(false)
    val tr by 'r'.toggle(false)
    val tz by 'z'.toggle(false)
    val ts by 's'.toggle(false)

    lateinit var mousePosBackup: XY
    lateinit var camPosBackup: XYZ
    lateinit var camRotBackup: XYZ
    var moving = false
    'c' trigger { if (moving) { camPosBackup = camPos; camRotBackup = camRot } }
    cmdMouseMove {
        val mousePos = it.x xy it.y
        if (!moving) mousePosBackup = mousePos
        val mousePosDelta = mousePos - mousePosBackup
        val factor = if (ts) 0.003 else 0.1
        when {
            !moving && (te || tr) -> {
                mousePosBackup = mousePos
                camPosBackup = camPos
                camRotBackup = camRot
                moving = true
            }
            te -> camPos = camPosBackup + mousePosDelta.toXYZ(swapYZ = tz) * factor
            tr -> camRot = camRotBackup + mousePosDelta.toXYZ(swapYZ = tz) * factor / 10.0
            moving -> { camPos = camPosBackup; camRot = camRotBackup; moving = false }
        }
    }


    H2 { UText("Kthreelhu JS") }
    UBoxedText("camPos:$camPos; camRot:$camRot; ts:$ts; tq:$te; tw:$tr tz:$tz", mono = true)
    KthExamples(camPos, camRot)
}

fun XY.toXYZ(z: Double = 0.0, swapYZ: Boolean = false) = if (swapYZ) x xy z yz y else x xy y yz z

suspend fun threeExperiment9() {
    console.log("TODO")
    window.alert("TODO")
//    val system = parseJsonBraxSystem("./walking_ant.json")
//    model?.scene?.addBraxSystem(system)
//    // TODO: trajectory/animation stuff (based on brax repo)
//    // TODO: review all brax stuff after something is moving..
}
