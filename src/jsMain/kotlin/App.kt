package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.channels.BufferOverflow.DROP_OLDEST
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.ExperimentalComposeWebWidgetsApi
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.renderComposable
import org.jetbrains.compose.web.ui.Styles
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
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

// TODO_NOW: try CompositionLocal to manage current Object3D objects in composition tree
// (for top level composables like "Scene" or "Renderer" use explicit receiver like "RendererScope" instead of additional CompositionLocal)
// (or maybe use explicit scopes for Object3D contexts too? so we don't overuse CompositionLocals? - try both approaches)

@OptIn(ExperimentalComposeWebWidgetsApi::class)
fun main() {
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    val root = document.getElementById("root") as HTMLElement

    val keyDownS = MutableSharedFlow<KeyboardEvent>(extraBufferCapacity = 1, onBufferOverflow = DROP_OLDEST)

    renderComposable(root = root) {
        var camPosX by remember { mutableStateOf(0.0f) }
        var camPosY by remember { mutableStateOf(0.0f) }
        var camPosZ by remember { mutableStateOf(10.0f) }

        LaunchedEffect(Unit) { threeExperiment1() }
        LaunchedEffect(camPosX) { model?.camera?.position?.x = camPosX }
        LaunchedEffect(camPosY) { model?.camera?.position?.y = camPosY }
        LaunchedEffect(camPosZ) { model?.camera?.position?.z = camPosZ }
        LaunchedEffect(Unit) {
            keyDownS.collect {
                when (it.key) {
                    "a" -> camPosX += 0.1f
                    "d" -> camPosX -= 0.1f
                    "w" -> camPosY -= 0.1f
                    "s" -> camPosY += 0.1f
                }
            }
        }
        DisposableEffect(Unit) {
            val callback: (Event) -> Unit = { keyDownS.tryEmit(it as KeyboardEvent) }
            window.addEventListener("keydown", callback)
            onDispose { window.removeEventListener("keydown", callback) }
        }
        Style(Styles)
        H2 { Text("Kthreelhu JS") }
        Text("camera: ${camPosX.toFixed()}, ${camPosY.toFixed()}, ${camPosZ.toFixed()}")
        Div(attrs = {
            onWheel {
                it.preventDefault()
                camPosZ += it.deltaY.toFloat() / 100
            }
            onMouseMove {
                if (it.buttons.toInt() != 0) {
                    camPosX = - it.offsetX.toFloat() / 100 + 5
                    camPosY = it.offsetY.toFloat() / 100 - 5
                }
            }
        }) {
            val scope = rememberCoroutineScope()
            Button(onClick = { scope.launch { threeExperiment2() }}) { Text("threeExperiment2") }
            Br()
            Div(attrs = { id("myscene") })
        }
    }
}

var model: MyThreeSceneModel? = null

fun threeExperiment1() {
    model = MyThreeSceneModel()
    model?.animate()
}

suspend fun threeExperiment2() {
    val system = parseJsonBraxSystem("./walking_ant.json")
    model?.scene?.addBraxSystem(system)
    // TODO: trajectory/animation stuff (based on brax repo)
    // TODO: review all brax stuff after something is moving..
}

class MyThreeSceneModel {
    private val clock = Clock()
    val camera = PerspectiveCamera(75, window.aspectRatio, 0.1, 1000).apply {
        position.z = 5
    }

    private val renderer = WebGLRenderer().apply {
        document.getElementById("myscene")?.appendChild(domElement)
        setSize(window.innerWidth / 2, window.innerHeight / 2)
        setPixelRatio(window.devicePixelRatio)
    }

    // phong material reacts to lights, but basic material does not
    private val cube1 = Mesh(BoxGeometry(1, 1, 1), MeshPhongMaterial().apply { color = Color(0x0000ff) })
    private val cube2 = Mesh(BoxGeometry(1, 2, 0.5), MeshPhongMaterial().apply { color = Color(0xff00ff) })

    var scene = Scene().apply {
        add(cube1.withGridHelper())
        add(cube2.withAxesHelper().withGridHelper())
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