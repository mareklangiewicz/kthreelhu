package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import pl.mareklangiewicz.widgets.CmnDText
import three.js.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable fun KthExamples(camPos: XYZ) {
    KthCamera {
        position.set(camPos)
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
    O3D({ DirectionalLight(0xffffff, 1) }) { position.set(-1.0 xy 2.0 yz 4.0) }
        // TODO: check position = Vect... (probably doesn't work - but why??)
    O3D({ AmbientLight(0x404040, 1) })
}

@Composable fun O3DExample1() {
    KthCube {
        KthGridHelper()
        material.color = remember { Color(0x0000ff) }
        EachFrameEffect { val t = it / 1000
            position.set(sin(t) * 4 xy cos(t * 1.4) * 7 yz sin(t * 5.7 + 2))
        }
    }
    KthCube(1.0 xy 2.0 yz 0.5) {
        KthGridHelper()
        KthAxesHelper()
        material.color = remember { Color(0xff00ff) }
    }
}

@Composable fun O3DExample2() {
    G3D {
        EachFrameEffect { rotation.set(it / 50000, -it / 20000, -it / 900) }
        for (x in 1..10) for (y in 1..10)
            KthCube(0.5 xy 0.5 yz remember { 1.0 rnd 3.0 }, Color(0xffffff.max)) {
                EachFrameEffect {
                    position.set(x.dbl * 0.7, y.dbl * 0.7, (0.00001 + it).max / 400000 * y)
                }
            }
    }
}

