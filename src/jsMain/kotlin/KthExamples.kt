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

class JsArrLike<T>(val jsObj: dynamic): Collection<T> {

    init { size } // read length to crash fast if jsObj doesn't look like array-like object

    operator fun <T> get(index: Int) = jsObj[index] as T

    override val size: Int get() = jsObj.length as Int

    override fun contains(element: T) = any { it == element }

    override fun containsAll(elements: Collection<T>) = elements.all { it in this }

    override fun isEmpty(): Boolean = size == 0

    override fun iterator(): Iterator<T> = object : Iterator<T> {
        var index = 0
        override fun hasNext(): Boolean = index < size
        override fun next(): T = this@JsArrLike[index++]
    }
}

private fun getGampads(): List<Gamepad> {
    val arr = JsArrLike<Gamepad?>(window.navigator.asDynamic().getGamepads())
    return buildList { for (pad in arr) pad?.let { add(it) } }
}

@Composable fun O3DExampleGamepad() {
    var gamepads by remember { mutableStateOf(emptyList<Gamepad>()) }
    Key("padadd").cmd().collect { gamepads = getGampads() }
    Key("padrem").cmd().collect { gamepads = getGampads() }

    CmnDColumn {
        if (gamepads.isEmpty()) CmnDText("no gamepads detected")
        else for (pad in gamepads) key(pad.id) {
            CmnDText("pad: index: ${pad.index}; id: ${pad.id}")
        }
    }
}

// https://developer.mozilla.org/en-US/docs/Web/API/Gamepad
external class Gamepad {
    val index: Int
    val id: String
}


