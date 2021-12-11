@file:OptIn(ExperimentalTime::class)

package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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


@Composable fun KthScene(
    create: () -> Scene = remember { { Scene() } },
    update: suspend Scene.() -> Unit = {},
    content: @Composable () -> Unit
) {
    val scene = remember(create) { create() }
    LaunchedEffect(update) { scene.update() }
    CompositionLocalProvider(LocalScene provides scene, LocalObject3D provides scene) { content() }
}

@Composable fun KthCamera(
    create: () -> PerspectiveCamera = remember { { createPerspectiveCamera() } }, // remember is needed here!
    update: suspend PerspectiveCamera.() -> Unit = {},
    content: @Composable () -> Unit
) {
    // Warning: doing just 'val camera = remember { create() }' would be incorrect!
    val camera = remember(create) { create() }
    LaunchedEffect(update) { camera.update() }
    CompositionLocalProvider(LocalCamera provides camera) { content() }
}

@Composable fun KthConfig(antialias: Boolean, content: @Composable () -> Unit = {}) {
    KthConfig({ canvas = it; this.antialias = antialias }, content)
}

// There is an issue with changing antialias reactively - see comment in fun createRenderer for details
// Workaround: wrap whole KthCanvas subtree in: key(antialias) { KthCanvas { ... } }
//   side note: recreating canvas and/or it's context is heavy and chrome complains in console after many antialias changes:
//     "WARNING: Too many active WebGL contexts. Oldest context will be lost."
@Composable fun KthConfig(
    config: WebGLRendererParameters.(HTMLCanvasElement) -> Unit = { canvas = it },
    content: @Composable () -> Unit = {}
) = CompositionLocalProvider(LocalRendererConfig provides config) { content() }

@Composable fun Kthreelhu() {
    val scene = LocalScene.current
    val camera = LocalCamera.current
    val config = LocalRendererConfig.current
    var renderer by remember { mutableStateOf<WebGLRenderer?>(null) }
    LocalCanvasScope.current.DisposableRefEffect(config) { canvas ->
        renderer = createRenderer(canvas, config)
        onDispose { renderer?.dispose(); renderer = null }
    }
    EachFrameEffect { renderer?.render(scene, camera) }
}

fun createPerspectiveCamera(
    fov: Int = 75,
    aspectRatio: Double = window.aspectRatio,
    near: Double = 0.1,
    far: Double = 1000.0
) = PerspectiveCamera(fov, aspectRatio, near, far)

fun createRenderer(
    canvas: HTMLCanvasElement,
    config: WebGLRendererParameters.(HTMLCanvasElement) -> Unit
): WebGLRenderer {
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    val params = (js("{}") as WebGLRendererParameters).apply { config(canvas) }
    println("params.antialias: ${params.antialias}")
    // TODO_later: antialias is not changing reactively on screen, but this println probably shows that I do it correctly:
    // because createRenderer is called every time and params have correct antialias set up every time. So probably a bug in three.js - investigate more..
    // I guess here is the explanation: https://developer.mozilla.org/en-US/docs/Web/API/HTMLCanvasElement/getContext
    // (It is not possible to get a different drawing context object on a given canvas element.)
    return WebGLRenderer(params).apply { setSize(canvas.clientWidth, canvas.clientHeight) }
}

@Composable fun <T: Object3D> O3D(
    create: () -> T,
    update: (suspend T.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    // it's important not to recreate objects unnecessarily (so let's leave console.log for each creation)
    val child = remember(create) { create().also { console.log("new Object3D! type:${it.type} hash:${it.hashCode()}") } }
    if (update != null) LaunchedEffect(update) { child.update() }
    val parent = LocalObject3D.current
    DisposableEffect(parent, child) {
        parent.add(child)
        onDispose { parent.remove(child) }
    }
    CompositionLocalProvider(LocalObject3D provides child) { content() }
}

@Composable fun G3D(
    update: (suspend Group.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val create = remember { { Group() } }
    O3D(create, update, content)
}


@Composable fun KthCube(
    size: XYZ = 1.0 xy 1.0 yz 1.0,
    color: Color = Color(0x808080),
    update: (suspend Mesh<BoxGeometry, MeshPhongMaterial>.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val create = remember(size, color) { { cube(size).apply { material.color = color } } }
    O3D(create, update, content)
}

@Composable fun KthLine2D(
    color: Color = Color(0x808080),
    vararg points: XY,
    update: (suspend Line<BufferGeometry, LineBasicMaterial>.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val create = remember(color, points) { { line2D(*points).apply { material.color = color } } }
    O3D(create, update, content)
}

@Composable fun KthLine3D(
    color: Color = Color(0x808080),
    vararg points: XYZ,
    update: (suspend Line<BufferGeometry, LineBasicMaterial>.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val create = remember(color, points) { { line3D(*points).apply { material.color = color } } }
    O3D(create, update, content)
}

@Composable fun KthGridHelper(
    units: Int = 10,
    depthTest: Boolean = false,
    renderOrder: Int = 1,
    gridColor1: Color = Color(0xffffff),
    gridColor2: Color = Color(0x888888),
    update: (suspend GridHelper.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val create = remember(units, depthTest, renderOrder, gridColor1, gridColor2) { {
        GridHelper(units, units, gridColor1, gridColor2).apply {
            material.depthTest = depthTest
            this.renderOrder = renderOrder
        }
    } }
    O3D(create, update, content)
}

@Composable fun KthAxesHelper(
    depthTest: Boolean = false,
    renderOrder: Int = 1,
    update: (suspend AxesHelper.() -> Unit)? = null,
    content: @Composable () -> Unit = {}
) {
    val create = remember(depthTest, renderOrder) { { AxesHelper().apply {
        material.depthTest = depthTest
        this.renderOrder = renderOrder
    } } }
    O3D(create, update, content)
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
