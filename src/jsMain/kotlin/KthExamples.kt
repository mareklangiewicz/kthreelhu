@file:OptIn(ExperimentalStdlibApi::class, ExperimentalTime::class)

package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import pl.mareklangiewicz.widgets.CmnDText
import pl.mareklangiewicz.widgets.kim.Kim.Companion.toggledLocally
import pl.mareklangiewicz.widgets.kim.Kim.Key
import three.js.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.DurationUnit.*
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable fun KthExamples(camPos: XYZ, camRot: XYZ) {
    KthCamera {
        position.set(camPos)
        rotation.set(camRot)
        // TODO: maybe use camera.lookAt instead of rotations
        val ex1full by Key("F").toggledLocally()
        val ex1 by Key("1").toggledLocally()
        val ex2 by Key("2").toggledLocally()
        CmnDText("Example 1 - press 1 to enable/disable", mono = true)
        if (ex1 || ex1full) KthScene {
//            Kthreelhu(attachTo = if (ex1full) window.document.body else null)
            // FIXME_later: above dynamic if makes moving around really slow.. why?
//            Kthreelhu(attachTo = window.document.body)
            Kthreelhu()
            O3DExampleLights()
            O3DExample1()
        }
        CmnDText("Example 2 - press 1 to enable/disable", mono = true)
        if (ex2) KthScene {
            Kthreelhu()
            O3DExampleLights()
            O3DExample2()
        }
    }
}

@Composable fun O3DExampleLights() {
    O3D({ DirectionalLight(0xffffff, 1) }) { position.set(-1.0 xy 2.0 yz 4.0) }
        // TODO: check position = Vect... (probably doesn't work - but why??)
    O3D({ AmbientLight(0x404040, 1) })
}

@Composable fun O3DExample1() {
    KthCube {
        KthGridHelper()
        material.color = remember { Color(0x0000ff) }
        EachFrameEffect {
            val t = it.toDouble(SECONDS)
            position.set(sin(t) * 4 xy cos(t * 1.4) * 7 yz sin(t * 5.7 + 2))
        }
    }
    KthCube(1.0 xy 2.0 yz 0.5) {
        KthGridHelper()
        KthAxesHelper()
        material.color = remember { Color(0xff00ff) }
    }
    val points2d = buildList {
        for (a in 1..30) add(sin(a.dbl * 3) * 20 xy cos(a.dbl * 3) * 20)
    }
    val points3d = points2d.mapIndexed { i, p -> p.toXYZ(i.dbl) + (0.0 xy 0.0 yz -30.0) }
    KthLine2D(Color(0x808080), *points2d.toTypedArray())
    KthLine3D(Color(0x0000f0), *points3d.toTypedArray())
}

@Composable fun O3DExample2() {
    G3D {
        EachFrameEffect {
            val t = it.toDouble(SECONDS)
            rotation.set(t / 50, -t / 20, -t)
        }
        for (x in 1..10) for (y in 1..10)
            KthCube(0.5 xy 0.5 yz remember { 1.0 rnd 3.0 }, Color(0xffffff.max)) {
                EachFrameEffect {
                    val t = it.toDouble(MILLISECONDS)
                    position.set(x.dbl * 0.7, y.dbl * 0.7, (0.00001 + t).max / 400000 * y)
                }
            }
    }
}

