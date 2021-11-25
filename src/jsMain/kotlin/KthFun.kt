package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import three.js.*

// TODO_later: for now all my composables here will have Kth prefix, to distinguish from three.js classes.
// I may drop these Kth prefixes later after some experiments/prototyping/iterations.
@Composable fun KthScene(content: @Composable Scene.() -> Unit) {
    val scene = remember { Scene() }
    CompositionLocalProvider(LocalScene provides scene, LocalObject3D provides scene) {
        scene.content()
    }
}

@Composable fun KthCamera(
    fov: Int = 75,
    aspectRatio: Double = window.aspectRatio,
    near: Double = 0.1,
    far: Double = 1000.0,
    content: @Composable Camera.() -> Unit
) {
    val camera = remember { PerspectiveCamera(fov, aspectRatio, near, far) }
    CompositionLocalProvider(LocalCamera provides camera) { camera.content() }
}

@Composable fun Kthreelhu(enabled: Boolean = true) = Div {
    val scene = LocalScene.current
    val camera = LocalCamera.current
    val renderer = remember {
        WebGLRenderer().apply {
            // TODO: design reasonable size related stuff
            setSize(window.innerWidth / 2, window.innerHeight / 2)
            setPixelRatio(window.devicePixelRatio)
        }
    }
    DisposableRefEffect(enabled) { element: HTMLDivElement ->
        element.appendChild(renderer.domElement)
        onDispose { element.removeChild(renderer.domElement) }
    }
    LaunchedEffect(enabled) {
        while (enabled) {
            window.awaitPaint()
            renderer.render(scene, camera)
        }
    }
}

@Composable fun <T: Object3D> O3D(newO3D: () -> T, content: @Composable T.() -> Unit = {}) {
    val child = remember(newO3D) { newO3D() }
    val parent = LocalObject3D.current
    DisposableEffect(parent, child) {
        parent.add(child)
        onDispose { parent.remove(child) }
    }
    CompositionLocalProvider(LocalObject3D provides child) { child.content() }
}

@Composable fun G3D(content: @Composable Group.() -> Unit) {
    O3D({ Group() }) { content() }
}


@Composable fun KthCube(
    width: Double = 1.0,
    height: Double = 1.0,
    depth: Double = 1.0,
    color: Color = Color(0x808080),
    content: @Composable Mesh<BoxGeometry, MeshPhongMaterial>.() -> Unit = {}
) {
    O3D({ cube(width, height, depth) }) { material.color = color; content() }
}

@Composable fun KthGridHelper(
    units: Int = 10,
    depthTest: Boolean = false,
    renderOrder: Int = 1,
    gridColor1: Color = Color(0xffffff),
    gridColor2: Color = Color(0x888888),
    content: @Composable GridHelper.() -> Unit = {}
) {
    O3D({ GridHelper(units, units, gridColor1, gridColor2) }) {
        material.depthTest = depthTest
        this.renderOrder = renderOrder
        content()
    }
}

@Composable fun KthAxesHelper(
    depthTest: Boolean = false,
    renderOrder: Int = 1,
    content: @Composable AxesHelper.() -> Unit = {}
) {
    O3D({ AxesHelper() }) {
        material.depthTest = depthTest
        this.renderOrder = renderOrder
        content()
    }
}

private fun cube(width: Double, height: Double, depth: Double) = cube(Vector3(width, height, depth))
private fun cube(size: Vector3) = Mesh(BoxGeometry(size.x, size.y, size.z), MeshPhongMaterial())

private val LocalObject3D = compositionLocalOf<Object3D> { error("No Object3D provided - start with fun KthScene") }
private val LocalScene = staticCompositionLocalOf<Scene> { error("No Scene provided - use fun KthScene") }
private val LocalCamera = staticCompositionLocalOf<Camera> { error("No Camera provided - use fun KthCamera") }