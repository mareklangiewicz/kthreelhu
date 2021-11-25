package pl.mareklangiewicz.kthreelhu

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.Window
import three.js.AxesHelper
import three.js.Color
import three.js.GridHelper
import three.js.Object3D


fun Float.toFixed(precision: Int = 2) = asDynamic().toFixed(precision)
fun Double.toFixed(precision: Int = 2) = asDynamic().toFixed(precision)

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight

@OptIn(ExperimentalCoroutinesApi::class)
/**
 * https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame
 * @return timestamp in millis since window creation
 */
suspend fun Window.awaitPaint(): Double = suspendCancellableCoroutine { cont ->
    val handle = requestAnimationFrame { cont.resume(it, null) }
    cont.invokeOnCancellation { cancelAnimationFrame(handle) }
}


operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.minus(that: Int) = toDouble() - that
operator fun Number.minus(that: Number) = toDouble() - that.toDouble()
operator fun Number.plus(other: Double) = toDouble() + other
operator fun Number.unaryMinus() = -toDouble()
operator fun Number.times(that: Int) = toDouble() * that
operator fun Number.div(that: Int) = toDouble() / that
operator fun Double.div(that: Number) = this / that.toDouble()


private val gridColor1 = Color(0xffffff)
private val gridColor2 = Color(0x888888)

fun <T: Object3D> T.withGridHelper(units: Int = 10, depthTest: Boolean = false, renderOrder: Int = 1) = apply {
    add(GridHelper(units, units, gridColor1, gridColor2).apply { this.material.depthTest = depthTest; this.renderOrder = renderOrder })
}

fun <T: Object3D> T.withAxesHelper(depthTest: Boolean = false, renderOrder: Int = 2) = apply {
    add(AxesHelper().apply { material.depthTest = depthTest; this.renderOrder = renderOrder })
}
