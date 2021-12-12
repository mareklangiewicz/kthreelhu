@file:OptIn(ExperimentalStdlibApi::class, ExperimentalTime::class)

package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.browser.window
import kotlinx.coroutines.isActive
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import pl.mareklangiewicz.widgets.CmnDColumn
import pl.mareklangiewicz.widgets.CmnDText
import pl.mareklangiewicz.widgets.kim.Kim.Companion.cmd
import pl.mareklangiewicz.widgets.kim.Kim.Companion.collect
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggledLocally
import pl.mareklangiewicz.widgets.kim.Kim.Key
import three.js.AmbientLight
import three.js.Color
import three.js.DirectionalLight
import kotlin.coroutines.coroutineContext
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.DurationUnit.MILLISECONDS
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable fun KthExamples(camPos: XYZ, camRot: XYZ) {
    KthCamera(update = {
        position.set(camPos)
        rotation.set(camRot)
    }) {
        // TODO: maybe use camera.lookAt instead of rotations
        val antialias by Key("A").toggledLocally()
        val ex1 by Key("1").toggledLocally()
        val ex2 by Key("2").toggledLocally()
        CmnDText("Example 1 - press 1 to enable/disable", mono = true)
        if (ex1) KthScene {
            key(antialias) { // workaround for issue commented for fun KthConfig
                KthCanvas(attrs = { style { width(60.percent) } }) {
                    KthConfig(antialias) {
                        Kthreelhu()
                    }
                }
            }
            O3DExampleLights()
            O3DExample1()
        }
        CmnDText("Example 2 - press 2 to enable/disable", mono = true)
        if (ex2) KthScene {
            key (antialias) { // workaround for issue commented for fun KthConfig
                KthConfig(antialias) {
                    KthCanvas(attrs = { style { width(60.percent) } }) {
                        Kthreelhu()
                    }
                }
            }
            O3DExampleLights()
            O3DExample2()
        }
    }
}

@Composable fun O3DExampleLights() {
    O3D({ DirectionalLight(0xffffff, 1) }, { position.set(-1.0 xy 2.0 yz 4.0) })
        // TODO: check position = Vect... (probably doesn't work - but why??)
    O3D({ AmbientLight(0x404040, 1) })
}

@Composable fun O3DExample1() {
    KthCube(
        color = Color(0x0000ff),
        update = {
            while (coroutineContext.isActive) withFrame {
                val t = it.toDouble(SECONDS)
                position.set((sin(t) * 4 xy cos(t * 1.4) * 7 yz sin(t * 5.7 + 2)))
            }
        }
    ) {
        KthGridHelper()
    }
    KthCube(1.0 xy 2.0 yz 0.5, Color(0xff00ff)) {
        KthGridHelper()
        KthAxesHelper()
    }
    val points2d = buildList { for (a in 1..30) add(sin(a.dbl * 3) * 20 xy cos(a.dbl * 3) * 20) }
    val points3d = points2d.mapIndexed { i, p -> p.toXYZ(i.dbl) + (0.0 xy 0.0 yz -30.0) }
    KthLine2D(Color(0x808080), *points2d.toTypedArray())
    KthLine3D(Color(0x0000f0), *points3d.toTypedArray())
    O3DExampleGamepad()
}

@Composable fun O3DExample2() {
    G3D(
        update = {
            while (coroutineContext.isActive) withFrame {
                val t = it.toDouble(SECONDS)
                rotation.set(t / 50 xy -t yz -t)
            }
        }
    ) {
        for (x in 1..10) for (y in 1..10)
            KthCube(
                size = 0.5 xy 0.5 yz remember { 1.0 rnd 3.0 },
                color = Color(0xffffff.max),
                update = {
                    while (coroutineContext.isActive) withFrame {
                        val t = it.toDouble(MILLISECONDS)
                        position.set(x.dbl * 0.7, y.dbl * 0.7, (0.00001 + t).max / 400000 * y)
                    }
                }
            )
    }
}

@Composable fun O3DExampleGamepad() {
    var gamepads by remember { mutableStateOf(emptyList<Gamepad>()) }
    Key("p").cmd().collect {
        for (g in gamepads) g.vibrationActuator?.play {
            strongMagnitude = 1.0
            weakMagnitude = 1.0
            duration = 2000.0
        }
    }
    Key("R").cmd().collect {
        gamepads = window.navigator.getGamepads()
        for (g in gamepads) g.vibrationActuator?.reset()
    }
    Key("padadd").cmd().collect { gamepads = window.navigator.getGamepads() }
    Key("padrem").cmd().collect { gamepads = window.navigator.getGamepads() }

    CmnDColumn {
        if (gamepads.isEmpty()) CmnDText("no gamepads detected")
        else for (pad in gamepads) key(pad.id) {
            CmnDColumn { pad.run {
                CmnDText("pad: index: $index; id: $id; timestamp: $timestamp")
                CmnDText("mapping:$mapping")
                CmnDText("connected:$connected")
                CmnDText("axes (size:${axes.size}):")
                CmnDColumn {
                    for (axis in axes) CmnDText("axis (-1 .. +1): $axis")
                }
                CmnDText("buttons (size:${buttons.size}):")
                CmnDColumn {
                    for (btn in buttons) {
                        CmnDText("touched: ${btn.touched}")
                        CmnDText("pressed: ${btn.pressed}")
                        CmnDText("value: ${btn.value}")
                    }
                }
            } }
        }
    }
}


