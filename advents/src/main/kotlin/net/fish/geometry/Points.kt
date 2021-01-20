package net.fish.geometry

import org.joml.Vector3f
import kotlin.math.sqrt

data class Point2D(
    val x: Double,
    val y: Double
) {
    operator fun plus(other: Point2D) = add(other)
    operator fun minus(other: Point2D) = subtract(other)
    operator fun times(k: Double) = scale(k)

    fun add(other: Point2D) = Point2D(x + other.x, y + other.y)
    fun subtract(other: Point2D) = Point2D(x - other.x, y - other.y)
    fun scale(k: Double) = Point2D(k * x, k * y)
}

data class Point3D(
    val x: Double,
    val y: Double,
    val z: Double
) {
    operator fun plus(other: Point3D) = add(other)
    operator fun minus(other: Point3D) = subtract(other)
    operator fun times(k: Double) = scale(k)
    operator fun div(k: Double) = scale(1.0 / k)

    fun add(other: Point3D) = Point3D(x + other.x, y + other.y, z + other.z)
    fun subtract(other: Point3D) = Point3D(x - other.x, y - other.y, z - other.z)
    fun scale(k: Double) = Point3D(k * x, k * y, k * z)
    fun length(): Double = sqrt(x * x + y * y + z * z)

    fun toVector3f(): Vector3f = Vector3f(x.toFloat(), y.toFloat(), z.toFloat())
}