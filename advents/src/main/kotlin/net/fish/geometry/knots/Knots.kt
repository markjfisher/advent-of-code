package net.fish.geometry.knots

import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Knots {
    // Parametric equations: (t = 0 .. 2π)
    // x(t) = cos(t) + 2cos(2t)
    // y(t) = sin(t) - 2sin(2t)
    // z(t) = 2sin(3t)
    //
    // r(t) = x(t) i + y(t) j + z(t) k
    // dr/dt = Tangent (direction) of curve
    // r'(t) = (-sin(t) - 4sin(2t)) i + (cos(t) - 4cos(2t)) j + 6cos(3t)
    // Normal is r''(t)
    // r''(t) = (-cos(t) - 8cos(2t)) i + (-sin(t) + 8sin(2t)) j - 18sin(3t)

    fun trefoilCoordinates(segments: Int): List<KnotData> {
        val step = 2 * PI / segments
        var t = 0.0
        return (0 until segments).map {
            val px = cos(t) + 2.0 * cos(2.0 * t)
            val py = sin(t) - 2.0 * sin(2.0 * t)
            val pz = 2.0 * sin(3.0 * t)

            val tx = -sin(t) - 4.0 * sin(2.0 * t)
            val ty = cos(t) - 4.0 * cos(2.0 * t)
            val tz = 6.0 * cos(3.0 * t)

            val nx = -cos(t) - 8.0 * cos(2.0 * t)
            val ny = -sin(t) + 8.0 * sin(2.0 * t)
            val nz = -18.0 * sin(3.0 * t)

            t += step
            KnotData(
                point = Vector3f(px.toFloat(), py.toFloat(), pz.toFloat()),
                tangent = Vector3f(tx.toFloat(), ty.toFloat(), tz.toFloat()).normalize(),
                normal = Vector3f(nx.toFloat(), ny.toFloat(), nz.toFloat()).normalize()
            )
        }
    }

    // Wikipedia formula:
    // x = sin(t) + 2sin(2t)
    // y = cos(t) - 2cos(2t)
    // z = -sin(3t)

    // r'(t)  = (cos(t) + 4cos(2t)) i + (-sin(t) + 4sin(2t)) j - 3cos(3t)
    // r''(t) = (-sin(t) -8sin(2t)) i + (-cos(t) + 8cos(2t)) j + 9sin(3t)

    fun wikiTrefoilCoordinates(segments: Int): List<KnotData> {
        val step = 2 * PI / segments
        var t = 0.0
        return (0 until segments).map {
            val px = sin(t) + 2.0 * sin(2.0 * t)
            val py = cos(t) - 2.0 * cos(2.0 * t)
            val pz = -sin(3.0 * t)

            val tx = cos(t) + 4.0 * cos(2.0 * t)
            val ty = -sin(t) + 4.0 * sin(2.0 * t)
            val tz = -3.0 * cos(3.0 * t)

            val nx = -sin(t) - 8.0 * sin(2.0 * t)
            val ny = -cos(t) + 8.0 * cos(2.0 * t)
            val nz = 9.0 * sin(3.0 * t)

            t += step
            KnotData(
                point = Vector3f(px.toFloat(), py.toFloat(), pz.toFloat()),
                tangent = Vector3f(tx.toFloat(), ty.toFloat(), tz.toFloat()).normalize(),
                normal = Vector3f(nx.toFloat(), ny.toFloat(), nz.toFloat()).normalize()
            )
        }
    }

    // see https://www.gsn-lib.org/docs/nodes/MeshTorusNode.php
    fun torusKnot(p: Int, q: Int, r: Double, segments: Int): List<KnotData> {
        val step = 2 * PI / segments
        var t = 0.0
        return (0 until segments).map {
            val px = (1.0 + r * cos(q * t)) * cos (p * t)
            val py = (1.0 + r * cos(q * t)) * sin (p * t)
            val pz = r * sin(q * t)

            val nx = cos(p * t) * cos(q * t)
            val ny = sin(p * t) * cos(q * t)
            val nz = sin(q * t)

            val tx = p * (1.0 + r * cos(q * t)) * (-sin(p * t)) + q * r * (-sin(q * t) * cos(p * t))
            val ty = p * (1.0 + r * cos(q * t)) * (cos(p * t)) + q * r * (-sin(q * t) * sin(p * t))
            val tz = q * r * cos(q * t)

            t += step
            KnotData(
                point = Vector3f(px.toFloat(), py.toFloat(), pz.toFloat()),
                tangent = Vector3f(tx.toFloat(), ty.toFloat(), tz.toFloat()).normalize(),
                normal = Vector3f(nx.toFloat(), ny.toFloat(), nz.toFloat()).normalize()
            )
        }
    }
}

data class KnotData(
    val point: Vector3f,
    val tangent: Vector3f,
    val normal: Vector3f
)