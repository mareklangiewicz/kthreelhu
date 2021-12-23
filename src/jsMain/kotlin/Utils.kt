@file:OptIn(ExperimentalTime::class, ExperimentalStdlibApi::class)

package pl.mareklangiewicz.kthreelhu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.produceState
import androidx.compose.runtime.withFrameNanos
import kotlinx.coroutines.isActive
import org.w3c.dom.Window
import three.js.Euler
import three.js.Vector3
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit.NANOSECONDS
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


fun Float.toFixed(precision: Int = 2) = asDynamic().toFixed(precision)
fun Double.toFixed(precision: Int = 2) = asDynamic().toFixed(precision)

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


val Double.int get() = toInt()
val Number.int get() = toInt()
val Int.dbl get() = toDouble()
val Number.dbl get() = toDouble()


infix fun Int.rnd(to: Int) = Random.nextInt(this, to + 1)
infix fun Double.rnd(to: Double) = Random.nextDouble(this, to)
val Int.max get() = 0 rnd this
val Double.max get() = 0.0 rnd this

fun Int.near(divisor: Int = 6) = this - this / divisor rnd this + this / divisor
fun Double.near(divisor: Double = 6.0) = this - this / divisor rnd this + this / divisor
fun Int.around(spread: Int = 6) = this + (-spread rnd spread)
fun Double.around(spread: Double = 6.0) = this + (-spread rnd spread)


data class XY(val x: Double = 0.0, val y: Double = 0.0) {
    override fun toString() = "(${x.toFixed()},${y.toFixed()})"
}
data class XYZ(val x: Double = 0.0, val y: Double = 0.0, val z: Double = 0.0) {
    override fun toString() = "(${x.toFixed()},${y.toFixed()},${z.toFixed()})"
}

infix fun Double.xy(that: Double) = XY(this, that)
infix fun XY.yz(that: Double) = XYZ(x, y, that)

operator fun XY.plus(that: XY) = x + that.x xy y + that.y
operator fun XY.minus(that: XY) = x - that.x xy y - that.y
operator fun XY.times(that: Double) = x * that xy y * that
operator fun XY.div(that: Double) = x / that xy y / that

operator fun XYZ.plus(that: XYZ) = x + that.x xy y + that.y yz z + that.z
operator fun XYZ.minus(that: XYZ) = x - that.x xy y - that.y yz z - that.z
operator fun XYZ.times(that: Double) = x * that xy y * that yz z * that
operator fun XYZ.times(that: XYZ) = x * that.x xy y * that.y yz z * that.z
operator fun XYZ.div(that: Double) = x / that xy y / that yz z / that
operator fun XYZ.div(that: XYZ) = x / that.z xy y / that.y yz z / that.z

fun lerp(v1: Double, v2: Double, fraction: Double = 0.5) = v1 + (v2 - v1) * fraction
fun lerp(p1: XY, p2: XY, fraction: Double = 0.5) = p1 + (p2 - p1) * fraction
fun lerp(p1: XYZ, p2: XYZ, fraction: Double = 0.5) = p1 + (p2 - p1) * fraction

infix fun XY.avg(that: XY) = lerp(this, that)
infix fun XYZ.avg(that: XYZ) = lerp(this, that)


infix fun XY.rnd(to: XY) = (x rnd to.x) xy (y rnd to.y)
infix fun XYZ.rnd(to: XYZ) = (x rnd to.x) xy (y rnd to.y) yz (z rnd to.z)

fun XY.near(divisor: Double = 6.0) = this - this / divisor rnd this + this / divisor
fun XYZ.near(divisor: Double = 6.0) = this - this / divisor rnd this + this / divisor

fun XY.around(spread: Double = 6.0) = this + ((-spread xy -spread) rnd (spread xy spread))
fun XYZ.around(spread: Double = 6.0) = this + ((-spread xy -spread yz -spread) rnd (spread xy spread yz spread))


fun Vector3.set(xyz: XYZ) = set(xyz.x, xyz.y, xyz.z)
fun Euler.set(xyz: XYZ) = set(xyz.x, xyz.y, xyz.z)
