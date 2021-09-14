package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Slider
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.ui.Styles
import org.w3c.dom.HTMLElement
import org.w3c.dom.Window
import three.js.AmbientLight
import three.js.BoxGeometry
import three.js.Clock
import three.js.Color
import three.js.DirectionalLight
import three.js.Mesh
import three.js.MeshPhongMaterial
import three.js.PerspectiveCamera
import three.js.Scene
import three.js.WebGLRenderer

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    val root = document.getElementById("root") as HTMLElement

    renderComposable(root = root) {
        var camPosX by mutableStateOf(0.0f)
        Style(Styles)
        H2 { Text("Kthreelhu JS") }
        Div {
            Button(onClick = ::threeExperiment) { Text("some button") }
            Br()
            Slider(camPosX, onValueChange = { camPosX = it; cube?.camera?.position?.x = it * 10 - 5 }, steps = 20)
            Div(attrs = { id("myscene") })
        }
    }
}

var cube: Cube? = null

fun threeExperiment() {
    cube = Cube()
    cube?.animate()
}

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight


operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.plus(other: Double) = toDouble() + other

class Cube {
    private val clock = Clock()
    val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 5
    }

    private val renderer = WebGLRenderer().apply {
        document.getElementById("myscene")?.appendChild(domElement)
        setSize(window.innerWidth / 2, window.innerHeight / 2)
        setPixelRatio(window.devicePixelRatio)
    }

    private val cube1 = Mesh(BoxGeometry(1, 1, 1), MeshPhongMaterial().apply { color = Color(0x0000ff) })
    private val cube2 = Mesh(BoxGeometry(1, 2, 0.5), MeshPhongMaterial().apply { color = Color(0xff00ff) })

    private val scene = Scene().apply {
        add(cube1)
        add(cube2)
        add(DirectionalLight(0xffffff, 1).apply { position.set(-1, 2, 4) })
        add(AmbientLight(0x404040, 1))
    }

    init {
        window.onresize = {
            camera.aspect = window.aspectRatio
            camera.updateProjectionMatrix()
            renderer.setSize(window.innerWidth / 2, window.innerHeight / 2)
        }
    }

    fun animate() {
        val delta = clock.getDelta().toDouble()
        cube1.rotation.x -= delta
        cube1.rotation.y -= delta
        cube2.rotation.x += delta / 2
        cube2.rotation.y += delta / 3
        renderer.render(scene, camera)
        window.requestAnimationFrame { animate() }
    }
}