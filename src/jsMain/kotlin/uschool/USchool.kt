@file:Suppress("OPT_IN_USAGE", "FunctionName")

/**
 * Micro school - experiment
 */

import androidx.compose.runtime.*
import kotlinx.browser.*
import org.jetbrains.compose.web.css.*
import org.w3c.dom.*
import pl.mareklangiewicz.kthreelhu.*
import three.js.*
import three.js.Color
import kotlin.math.*

@JsExport
class UObject3D internal constructor(private val o3d: Object3D) {
    val x get() = o3d.position.x.dbl
    val y get() = o3d.position.y.dbl
    val z get() = o3d.position.z.dbl
    fun pos(x: Double, y: Double, z: Double): UObject3D = apply { o3d.position.set(x, y, z) }
    fun add(uobject3D: UObject3D) { o3d.add(uobject3D.o3d) }
    fun del(uobject3D: UObject3D) { o3d.remove(uobject3D.o3d) }
}

@JsExport
class USchool {

    internal val sceneObject3D = Scene()

    val scene: UObject3D = UObject3D(sceneObject3D)

    // TODO_later: Install only KthSchool(this)
    fun tryToInstallSchoolIn(rootElement: Element?) = tryToInstallAppIn(rootElement)
    fun tryToInstallSchoolInBody(clearBody: Boolean = false) { document.body?.run {
        if (clearBody) while (true) lastChild?.let { removeChild(it) } ?: break
        tryToInstallSchoolIn(this)
    } ?: error("document.body is null") }

    fun ucube(sizeX: Double, sizeY: Double, sizeZ: Double, color: Int = 0x808080): UObject3D =
        UObject3D(cube(sizeX xy sizeY yz sizeZ).apply { material.color = Color(color) })

    fun uline2D(color: Int, vararg xys: Double): UObject3D =
        UObject3D(line2D(*xys.windowedXYs()).apply { material.color = Color(color) })

    fun uline3D(color: Int, vararg xyzs: Double): UObject3D =
        UObject3D(line3D(*xyzs.windowedXYZs()).apply { material.color = Color(color) })

    private fun DoubleArray.windowedXYs() = toList().windowed(2, 2) { it[0] xy it[1] }.toTypedArray()
    private fun DoubleArray.windowedXYZs() = toList().windowed(3, 3) { it[0] xy it[1] yz it[2] }.toTypedArray()

    fun example1AddSpiral() {
        for (a in 1..200) scene.add(
            ucube(0.2, 0.2, 0.2, niceColorInt(a))
                .pos(a.dbl / 20, sin(a.dbl / 10) * 3, -cos(a.dbl / 10) * 3)
        )
    }

    fun niceColorInt(idx: Int, offset: Int = 0, factor: Int = 1): Int {
        val i = (idx + offset) * factor % 0x100
        val red = i * 3 % 0x100
        val green = i
        val blue = 0x100 - i
        return 0x010000 * red + 0x000100 * green + 0x000001 * blue
    }
}

@JsExport
val uschool: USchool = USchool()


@Composable internal fun KthSchool(school: USchool = uschool) {
    KthScene(create = { school.sceneObject3D }) {
        KthCanvasForSchool {
            KthConfig(antialias = true) {
                Kthreelhu()
            }
        }
        O3DExampleLights()
        O3DSchool()
    }
    LaunchedEffect(Unit) { console.log(school.sceneObject3D) }
}

@Composable private fun KthCanvasForSchool(content: @Composable () -> Unit = {}) {
    val found = document.getElementById("canvasForSchool") as? HTMLCanvasElement
    if (found == null) KthCanvas({ style { width(80.percent) } }, content)
    else KthCanvasFromOutside(found, content)
}

@Composable private fun O3DSchool() {
    KthCube(1.0 xy 1.0 yz 1.0, Color(0x02a0a0d0)) {
        KthGridHelper()
        KthAxesHelper()
    }
}
