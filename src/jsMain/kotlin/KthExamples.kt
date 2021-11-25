package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.browser.window
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.dom.Text
import pl.mareklangiewicz.widgets.CmnDText
import three.js.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable fun KthExamples(camPosX: Double, camPosY: Double, camPosZ: Double) {
    KthCamera {
        position.set(camPosX, camPosY, camPosZ)
        CmnDText("Example 1", mono = true)
        KthScene {
            Kthreelhu()
            O3DExampleLights()
            O3DExample1()
        }
        CmnDText("Example 2", mono = true)
        KthScene {
            Kthreelhu()
            O3DExampleLights()
            O3DExample2()
        }
    }
}

@Composable fun O3DExampleLights() {
    O3D({ DirectionalLight(0xffffff, 1) }) { position.set(-1, 2, 4) }
        // TODO: check position = Vect... (probably doesn't work - but why??)
    O3D({ AmbientLight(0x404040, 1) })
}

@Composable fun O3DExample1() {
    KthCube {
        KthGridHelper()
        material.color = remember { Color(0x0000ff) }
        val timeMs by window.produceTime()
        val t = timeMs / 1000
        position.set(sin(t) * 4, cos(t * 1.4) * 7, sin(t * 5.7 + 2))
    }
    KthCube(1.0, 2.0, 0.5) {
        KthGridHelper()
        KthAxesHelper()
        material.color = remember { Color(0xff00ff) }
    }
}

@Composable fun O3DExample2() {
    val timeMs by window.produceTime()
    G3D {
        for (x in 1..10) for (y in 1..10) KthCube(
            0.5, 0.5, 3.0 + Random.nextDouble(3.0),
            Color(Random.nextInt(0xffffff))
        ) { position.set(x.toDouble() * 0.7, y.toDouble() * 0.7, Random.nextDouble(timeMs / 400000 + 0.00001)) }
    }
}

