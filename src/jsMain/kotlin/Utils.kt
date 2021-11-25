package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.Window


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

@Composable fun Window.produceTime() = produceState(0.0) {
    while (true) value = awaitPaint()
}

@Composable fun Window.onEachFrame(action: (Double) -> Unit) = LaunchedEffect(Unit) {
    while (true) action(awaitPaint())
}


operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.minus(that: Int) = toDouble() - that
operator fun Number.minus(that: Number) = toDouble() - that.toDouble()
operator fun Number.plus(other: Double) = toDouble() + other
operator fun Number.unaryMinus() = -toDouble()
operator fun Number.times(that: Int) = toDouble() * that
operator fun Number.div(that: Int) = toDouble() / that
operator fun Double.div(that: Number) = this / that.toDouble()

