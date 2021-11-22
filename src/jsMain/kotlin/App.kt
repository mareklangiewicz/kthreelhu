@file:OptIn(ExperimentalComposeWebWidgetsApi::class)
package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.document
import kotlinx.browser.window
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
import pl.mareklangiewicz.widgets.CmnDText
import pl.mareklangiewicz.widgets.kim.Kim
import pl.mareklangiewicz.widgets.kim.KimKeyDownEffect
import three.js.*

// TODO_NOW: try CompositionLocal to manage current Object3D objects in composition tree
// (for top level composables like "Scene" or "Renderer" use explicit receiver like "RendererScope" instead of additional CompositionLocal)
// (or maybe use explicit scopes for Object3D contexts too? so we don't overuse CompositionLocals? - try both approaches)

fun main() {
    console.log("Kotlin version: ${KotlinVersion.CURRENT}")
    val root = document.getElementById("root") as HTMLElement
    renderComposable(root = root) { AppJs() }
}

@Composable fun AppJs() {
    Style(Styles)
    Kim.Area {
        val kim = Kim.kim
        KimKeyDownEffect(kim, window)
        kim.Cmd("q") { window.close() }
        Kim.Frame { AppContent() }
    }
}

@Composable private fun AppContent() {
    var camPosX by remember { mutableStateOf(0.0f) }
    var camPosY by remember { mutableStateOf(0.0f) }
    var camPosZ by remember { mutableStateOf(10.0f) }

    val kim = Kim.kim
    kim.Cmd("h") { camPosX += 0.1f }
    kim.Cmd("l") { camPosX -= 0.1f }
    kim.Cmd("k") { camPosY -= 0.1f }
    kim.Cmd("j") { camPosY += 0.1f }
    kim.Cmd("i") { camPosZ -= 0.1f }
    kim.Cmd("o") { camPosZ += 0.1f }

    LaunchedEffect(Unit) { threeExperiment1() }
    LaunchedEffect(camPosX) { model?.camera?.position?.x = camPosX }
    LaunchedEffect(camPosY) { model?.camera?.position?.y = camPosY }
    LaunchedEffect(camPosZ) { model?.camera?.position?.z = camPosZ }

    H2 { Text("Kthreelhu JS") }
    CmnDText("camera: ${camPosX.toFixed()}, ${camPosY.toFixed()}, ${camPosZ.toFixed()}", header = true)
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