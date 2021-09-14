package pl.mareklangiewicz.kthreelhu

import org.jetbrains.compose.web.renderComposable
import kotlinx.browser.document
import org.jetbrains.compose.common.material.Button
import org.w3c.dom.HTMLElement
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.ui.Styles
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.Window
import three.js.*

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    val root = document.getElementById("root") as HTMLElement

    renderComposable(root = root) {
        Style(Styles)
        H2 {
            Text("Kthreelhu JS")
            Button(onClick = ::threeExperiment) {
                Text("some button")
            }
            Div(attrs = {
                id("myscene")
            }) {  }
        }
    }
}

fun threeExperiment() {
    Cube().animate()
}

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight


operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.plus(other: Double) = toDouble() + other

class Cube {
    private val clock = Clock()
    private val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 5
    }

    private val renderer = WebGLRenderer().apply {
        document.getElementById("myscene")?.appendChild(domElement)
        setSize(window.innerWidth / 2, window.innerHeight / 2)
        setPixelRatio(window.devicePixelRatio)
    }

    private val cube = Mesh(BoxGeometry(1, 1, 1), MeshPhongMaterial().apply { color = Color(0x0000ff) })

    private val scene = Scene().apply {
        add(cube)
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
        cube.rotation.x -= delta
        cube.rotation.y -= delta
        renderer.render(scene, camera)
        window.requestAnimationFrame { animate() }
    }
}