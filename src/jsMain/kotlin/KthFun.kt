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

@Composable fun KthRenderer(content: @Composable () -> Unit) {
    val renderer = remember { WebGLRenderer() }
    CompositionLocalProvider(LocalRenderer provides renderer) { content() }
}

@Composable fun KthDivCanvas(enabled: Boolean = true, content: @Composable () -> Unit = {}) {
    content()
    val scene = LocalScene.current
    val camera = LocalCamera.current
    val renderer = LocalRenderer.current
    var handleId: Int
    fun animate() {
        renderer.render(scene, camera)
        handleId = window.requestAnimationFrame { animate() }
    }
    Div {
        DisposableRefEffect(enabled) { element: HTMLDivElement ->

            // TODO: design reasonable size related stuff
            renderer.setSize(window.innerWidth / 2, window.innerHeight / 2)
            renderer.setPixelRatio(window.devicePixelRatio)

            element.appendChild(renderer.domElement)
            handleId = window.requestAnimationFrame { animate() }
            onDispose {
                handleId.let { window.cancelAnimationFrame(it) }
                element.removeChild(renderer.domElement)
            }
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

private val LocalObject3D = compositionLocalOf<Object3D> { error("No Object3D provided - start with fun KthScene") }
private val LocalScene = staticCompositionLocalOf<Scene> { error("No Scene provided - use fun KthScene") }
private val LocalCamera = staticCompositionLocalOf<Camera> { error("No Camera provided - use fun KthCamera") }
private val LocalRenderer = staticCompositionLocalOf<WebGLRenderer> { error("No Renderer provided - use fun KthRenderer") }