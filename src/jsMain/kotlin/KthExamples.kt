package pl.mareklangiewicz.kthreelhu

import KthSchool
import androidx.compose.runtime.*
import kotlinx.browser.*
import kotlinx.coroutines.*
import org.jetbrains.compose.web.css.*
import pl.mareklangiewicz.gamepad.*
import pl.mareklangiewicz.umath.*
import pl.mareklangiewicz.upue.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.widgets.*
import pl.mareklangiewicz.widgets.kim.Kim.Companion.cmdPadChange
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggle
import pl.mareklangiewicz.widgets.kim.Kim.Companion.trigger
import three.js.*
import three.js.Color
import kotlin.coroutines.*
import kotlin.math.*
import kotlin.time.DurationUnit.*

@Composable fun KthExamples(camPos: XYZ, camRot: XYZ) {
    KthCamera(camPos, camRot) {
        // TODO: maybe use camera.lookAt instead of rotations
        val antialias by 'a'.toggle()
        val ex0 by '0'.toggle(true)
        val ex1 by '1'.toggle()
        val ex2 by '2'.toggle()
        val ex3 by '3'.toggle()
        val ex4 by '4'.toggle()
        UText("Example 0 .. 4 - press 0 .. 4 to enable/disable", mono = true)
        if (ex0) KthSchool()
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
        if (ex3) Example3GamepadsDOM()
        if (ex4) Example4FlyingGamepad()
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
                color = Color(0 rnd 0xffffff),
                update = {
                    while (coroutineContext.isActive) withFrame {
                        val t = it.toDouble(MILLISECONDS)
                        position.set(x.dbl * 0.7, y.dbl * 0.7, (0.00001 rnd t) / 400000 * y)
                    }
                }
            )
    }
}

@Composable fun Example3GamepadsDOM() {
    var gamepads by remember { mutableStateOf(arrOf<Gamepad?>()) }
    EachFrameEffect { gamepads = window.navigator.getGamepads() } // bad because we allocate JsArr..
    cmdPadChange { gamepads = window.navigator.getGamepads() } // not useful because we do it in EachFrameEffect anyway
    'p' trigger {
        for (g in gamepads) g?.vibrationActuator?.play {
            strongMagnitude = 1.0
            weakMagnitude = 1.0
            duration = 2000.0
        }
    }
    'R' trigger {
        for (g in gamepads) g?.vibrationActuator?.reset()
    }

    UColumn {
        if (gamepads.len == 0) UBoxedText("no gamepads detected")
        else for (pad in gamepads) if (pad != null) key(pad.id) {
            UColumn { pad.run {
                UBoxedText("pad: index: $index; id: $id; timestamp: $timestamp")
                UBoxedText("connected:$connected; mapping:$mapping")
                UBoxedText("axes (${axes.size}):", mono = true)
                UColumn {
                    for (axis in axes) CmnDProgress(axis, -1.0, 1.0, bold = abs(axis) > 0.1)
                }
                UBoxedText("buttons (${buttons.size}):", mono = true)
                UColumn {
                    for (btn in buttons) {
                        CmnDProgress(btn.value, 0.0, 1.0, bold = btn.touched || btn.pressed)
                    }
                }
            } }
        }
    }
}

@Composable fun Example4FlyingGamepad() = KthScene {

    var camPos by remember { mutableStateOf(XYZ(0.0, 0.0, 20.0)) }
    var camRot by remember { mutableStateOf(XYZ(0.0, 0.0, 0.0)) }

    var gamepads by remember { mutableStateOf(arrOf<Gamepad?>()) }
    EachFrameEffect {
        gamepads = window.navigator.getGamepads()
        gamepads.filterNotNull().firstOrNull()?.let {
            val dx = it.axes[0]
            val dy = -it.axes[1]
            val dz = it.buttons[6].value * if (it.buttons[4].pressed) -1 else 1
            camPos += XYZ(dx, dy, dz)

            val drx = it.axes[2] / 100
            val dry = it.axes[3] / 100
            val drz = it.buttons[7].value / 100 * if (it.buttons[5].pressed) -1 else 1
            camRot += XYZ(dry, drx, drz)
        }
    }

    KthCamera(camPos, camRot) {
        KthCanvas(attrs = { style { width(60.percent) } }) {
            KthConfig(antialias = false) {
                Kthreelhu()
            }
        }
    }
    O3DExampleLights()
    O3DExample1()
}

