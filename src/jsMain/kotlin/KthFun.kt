@file:OptIn(ExperimentalTime::class)

package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.ContentBuilder
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.ElementScope
import org.jetbrains.compose.web.dom.TagElement
import org.w3c.dom.HTMLCanvasElement
import three.js.AxesHelper
import three.js.BoxGeometry
import three.js.BufferGeometry
import three.js.Camera
import three.js.Color
import three.js.GridHelper
import three.js.Group
import three.js.Line
import three.js.LineBasicMaterial
import three.js.Mesh
import three.js.MeshPhongMaterial
import three.js.Object3D
import three.js.PerspectiveCamera
import three.js.Renderer
import three.js.Scene
import three.js.Vector2
import three.js.Vector3
import three.js.WebGLRenderer
import three.js.WebGLRendererParameters
import kotlin.time.ExperimentalTime


// FIXME_later: I don't know why sth like this is not already in standard:
// web-core-js-1.0.0-sources.jar!/jsMain/org/jetbrains/compose/web/elements/Elements.kt:96
private class CanvasBuilder : ElementBuilder<HTMLCanvasElement> {
    override fun create() = document.createElement("canvas") as HTMLCanvasElement
}

@Composable
fun Canvas(attrs: AttrBuilderContext<HTMLCanvasElement>? = null, content: ContentBuilder<HTMLCanvasElement>? = null) {
    TagElement(CanvasBuilder(), attrs, content)
}

@Composable
fun KthCanvas(attrs: AttrBuilderContext<HTMLCanvasElement>? = null, content: @Composable () -> Unit = {}) {
    Canvas(attrs) {
        CompositionLocalProvider(LocalCanvasScope provides this) { content() }
    }
}


// TODO_later: for now all my composables here will have Kth prefix, to distinguish from three.js classes.
// I may drop these Kth prefixes later after some experiments/prototyping/iterations.
@Composable fun KthScene(content: @Composable Scene.() -> Unit) {
    val scene = remember { Scene() }
    CompositionLocalProvider(LocalScene provides scene, LocalObject3D provides scene) { scene.content() }
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

@Composable fun KthRendererConfig(
    config: WebGLRendererParameters.(HTMLCanvasElement) -> Unit = { canvas = it },
    content: @Composable () -> Unit = {}
) = CompositionLocalProvider(LocalRendererConfig provides config) { content() }

@Composable fun Kthreelhu() {
    val scene = LocalScene.current
    val camera = LocalCamera.current
    val config = LocalRendererConfig.current
    var kthCanvas by remember { mutableStateOf<HTMLCanvasElement?>(null) }
    LocalCanvasScope.current.DisposableRefEffect {
        kthCanvas = it
        onDispose { kthCanvas = null }
    }
    val renderer by remember(scene, camera, config, kthCanvas) {
        lazy<Renderer> {
            @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
            val params = (js("{}") as WebGLRendererParameters)
                .apply { config(kthCanvas ?: error("Lazy BS logic failed")) }
            WebGLRenderer(params).apply {
                setSize(kthCanvas!!.clientWidth, kthCanvas!!.clientHeight)
            }
        }
    }
    EachFrameEffect(config) { renderer.render(scene, camera) }
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
private val LocalRendererConfig = staticCompositionLocalOf<WebGLRendererParameters.(HTMLCanvasElement) -> Unit> { { canvas = it } }
private val LocalCanvasScope = staticCompositionLocalOf<ElementScope<HTMLCanvasElement>> { error("No Canvas provided - use fun KthCanvas") }
