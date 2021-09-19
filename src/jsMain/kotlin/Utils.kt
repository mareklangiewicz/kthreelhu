package pl.mareklangiewicz.kthreelhu

import org.w3c.dom.Window


fun Float.toFixed(precision: Int = 2) = asDynamic().toFixed(precision)

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight


operator fun Number.minus(other: Double) = toDouble() - other
operator fun Number.minus(that: Int) = toDouble() - that
operator fun Number.minus(that: Number) = toDouble() - that.toDouble()
operator fun Number.plus(other: Double) = toDouble() + other
operator fun Number.unaryMinus() = -toDouble()
operator fun Number.times(that: Int) = toDouble() * that
operator fun Number.div(that: Int) = toDouble() / that
operator fun Double.div(that: Number) = this / that.toDouble()
