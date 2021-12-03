@file:OptIn(ExperimentalTime::class)

package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.Node
import three.js.*
import kotlin.time.ExperimentalTime

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

@Composable fun KthRenderer(
    setup: WebGLRendererParameters.() -> Unit = {},
    content: @Composable WebGLRenderer.() -> Unit
) {
    val renderer = remember(setup) {
        @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
        val params = (js("{}") as WebGLRendererParameters).apply(setup)
        WebGLRenderer(params)
    }
    CompositionLocalProvider(LocalRenderer provides renderer) { renderer.content() }
}

/**
 * @param attachTo null means it should create own Div element and append renderer canvas to it
 */
@Composable fun Kthreelhu(enabled: Boolean = true, attachTo: Node? = null) {
    val scene = LocalScene.current
    val camera = LocalCamera.current
    val renderer = LocalRenderer.current
    if (attachTo != null) DisposableEffect(renderer) {
        attachTo.appendChild(renderer.domElement)
        onDispose { attachTo.removeChild(renderer.domElement) }
    }
    else
        Div { DisposableRefEffect(renderer) { element: HTMLDivElement ->
        element.appendChild(renderer.domElement)
        onDispose { element.removeChild(renderer.domElement) }
    } }
    if (enabled) EachFrameEffect { renderer.render(scene, camera) }
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
    size: XYZ = 1.0 xy 1.0 yz 1.0,
    color: Color = Color(0x808080),
    content: @Composable Mesh<BoxGeometry, MeshPhongMaterial>.() -> Unit = {}
) {
    O3D({ cube(size) }) { material.color = color; content() }
}

@Composable fun KthLine2D(
    color: Color = Color(0x808080),
    vararg points: XY,
    content: @Composable Line<BufferGeometry, LineBasicMaterial>.() -> Unit = {}
) {
    O3D({ line2D(*points) }) { material.color = color; content() }
}

@Composable fun KthLine3D(
    color: Color = Color(0x808080),
    vararg points: XYZ,
    content: @Composable Line<BufferGeometry, LineBasicMaterial>.() -> Unit = {}
) {
    O3D({ line3D(*points) }) { material.color = color; content() }
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

private fun cube(size: XYZ) = Mesh(BoxGeometry(size.x, size.y, size.z), MeshPhongMaterial())
private fun line2D(vararg points: XY) = Line(lineGeo2D(*points), LineBasicMaterial())
private fun line3D(vararg points: XYZ) = Line(lineGeo3D(*points), LineBasicMaterial())
private fun lineGeo2D(vararg points: XY) = BufferGeometry().setFromPoints(points.map { it.toVector2() }.toTypedArray())
private fun lineGeo3D(vararg points: XYZ) = BufferGeometry().setFromPoints(points.map { it.toVector3() }.toTypedArray())


private fun XY.toVector2() = Vector2(x, y)
private fun XYZ.toVector3() = Vector3(x, y, z)

private val LocalObject3D = compositionLocalOf<Object3D> { error("No Object3D provided - start with fun KthScene") }
private val LocalScene = staticCompositionLocalOf<Scene> { error("No Scene provided - use fun KthScene") }
private val LocalCamera = staticCompositionLocalOf<Camera> { error("No Camera provided - use fun KthCamera") }
private val LocalRenderer = staticCompositionLocalOf<WebGLRenderer> { error("No Renderer provided - use fun KthRenderer") }