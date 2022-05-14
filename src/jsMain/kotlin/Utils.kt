package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.*
import kotlinx.coroutines.*
import org.w3c.dom.*
import pl.mareklangiewicz.umath.*
import three.js.*
import kotlin.time.*
import kotlin.time.DurationUnit.*


val Window.aspectRatio get() = innerWidth.dbl / innerHeight

inline suspend fun <R> withFrame(crossinline onFrame: (frameTime: Duration) -> R): R =
    withFrameNanos { onFrame(it.toDuration(NANOSECONDS)) }

@Composable fun produceEachFrameTime() = produceState(Duration.ZERO) {
    while (isActive) value = withFrame { it }
}

@Composable fun EachFrameEffect(key: Any? = null, onEachFrame: (Duration) -> Unit) =
    LaunchedEffect(key, onEachFrame) {
        while (isActive) onEachFrame(withFrame { it })
    }

fun Vector3.set(xyz: XYZ) = set(xyz.x, xyz.y, xyz.z)
fun Euler.set(xyz: XYZ) = set(xyz.x, xyz.y, xyz.z)
