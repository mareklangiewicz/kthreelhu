package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.browser.window
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import three.js.*
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalComposeWebWidgetsApi::class)
@Composable fun KthExample1(camPosX: Double, camPosY: Double, camPosZ: Double) {
    KthCamera {
        position.set(camPosX, camPosY, camPosZ)
        KthScene {
            Kthreelhu()
            O3DExample1()
        }
    }
}

@Composable fun O3DExample1() {
    O3D({ cube(1.0, 1.0, 1.0).withGridHelper() }) {
        material.color = Color(0x0000ff)
        LaunchedEffect(true) {
            while(true) {
                val t = window.awaitPaint() / 1000
                position.setX(sin(t)*4)
                position.setY(cos(t*1.4)*7)
            }
        }
    }
    O3D({ cube(1.0, 2.0, 0.5).withAxesHelper().withGridHelper() }) {
        material.color = Color(0xff00ff)
    }
    O3D({ DirectionalLight(0xffffff, 1) }) {
        position.set(-1, 2, 4) // TODO: check position = Vect... (probably doesn't work - but why??)
    }
    O3D({ AmbientLight(0x404040, 1) })
}

private fun cube(width: Double, height: Double, depth: Double) = cube(Vector3(width, height, depth))
private fun cube(size: Vector3) = Mesh(BoxGeometry(size.x, size.y, size.z), MeshPhongMaterial())
