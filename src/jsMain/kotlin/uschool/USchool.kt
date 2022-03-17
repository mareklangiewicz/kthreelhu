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
class UObject3D internal constructor(internal val object3D: Object3D) {
    val x get() = object3D.position.x.dbl
    val y get() = object3D.position.y.dbl
    val z get() = object3D.position.z.dbl
    fun move(x: Double, y: Double, z: Double) { object3D.position.set(x, y, z) }
    fun add(vararg objs: UObject3D) { object3D.add(*objs.map { it.object3D }.toTypedArray()) }
    fun del(vararg objs: UObject3D) { object3D.remove(*objs.map { it.object3D }.toTypedArray()) }
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

    fun school1example() {
        for (a in 1..90) {
            val c = ucube(0.2, 0.2, 0.2)
            c.move(a.dbl / 10, sin(a.dbl / 10) * 3, -cos(a.dbl / 10) * 3)
            scene.add(c)
        }
    }
}

@JsExport
val uschool: USchool = USchool()


@Composable internal fun KthSchool(school: USchool = uschool) {
    KthScene(create = { school.sceneObject3D }) {
        KthCanvas(attrs = { style { width(60.percent) } }) {
            KthConfig(antialias = true) {
                Kthreelhu()
            }
        }
        O3DExampleLights()
        O3DSchool()
    }
    LaunchedEffect(Unit) { console.log(school.sceneObject3D) }
}

@Composable private fun O3DSchool() {
    KthCube(1.0 xy 1.0 yz 1.0, Color(0x02a0a0d0)) {
        KthGridHelper()
        KthAxesHelper()
    }
}
