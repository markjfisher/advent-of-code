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
        return (0 until segments).map { i ->
            val x = i * 2.0 * PI / segments
            val px = cos(x) + 2.0 * cos(2.0 * x)
            val py = sin(x) - 2.0 * sin(2.0 * x)
            val pz = 2.0 * sin(3.0 * x)

            val tx = -sin(x) - 4.0 * sin(2.0 * x)
            val ty = cos(x) - 4.0 * cos(2.0 * x)
            val tz = 6.0 * cos(3.0 * x)

            val nx = -cos(x) - 8.0 * cos(2.0 * x)
            val ny = -sin(x) + 8.0 * sin(2.0 * x)
            val nz = -18.0 * sin(3.0 * x)

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }

    // Wikipedia formula:
    // x = sin(t) + 2sin(2t)
    // y = cos(t) - 2cos(2t)
    // z = -sin(3t)

    // r'(t)  = (cos(t) + 4cos(2t)) i + (-sin(t) + 4sin(2t)) j - 3cos(3t)
    // r''(t) = (-sin(t) -8sin(2t)) i + (-cos(t) + 8cos(2t)) j + 9sin(3t)

    fun wikiTrefoilCoordinates(segments: Int): List<KnotData> {
        return (0 until segments).map { i ->
            val x = i * 2.0 * PI / segments
            val px = sin(x) + 2.0 * sin(2.0 * x)
            val py = cos(x) - 2.0 * cos(2.0 * x)
            val pz = -sin(3.0 * x)

            val tx = cos(x) + 4.0 * cos(2.0 * x)
            val ty = -sin(x) + 4.0 * sin(2.0 * x)
            val tz = -3.0 * cos(3.0 * x)

            val nx = -sin(x) - 8.0 * sin(2.0 * x)
            val ny = -cos(x) + 8.0 * cos(2.0 * x)
            val nz = 9.0 * sin(3.0 * x)

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }

    // 4b from
    // https://www.mi.sanu.ac.rs/vismath/taylorapril2011/Taylor.pdf
    fun decoratedKnot4b(segments: Int): List<KnotData> {
        return (0 until segments).map { i ->
            val x = i * 2.0 * PI / segments
            val px = cos(2.0 * x) * (1.0 + 0.45 * cos(3.0 * x) + 0.4 * cos(9 * x))
            val py = sin(2.0 * x) * (1.0 + 0.45 * cos(3.0 * x) + 0.4 * cos(9 * x))
            val pz = 0.2 * sin(9.0 * x)

            // Use https://www.derivative-calculator.net/ to calculate the derivatives
            val tx = -(88 * sin(11 * x) + 56 * sin(7 * x) + 45 * sin(5 * x) + 80 * sin(2 * x) + 9 * sin(x)) / 40.0
            val ty = (88 * cos(11 * x) - 56 * cos(7 * x) + 45 * cos(5 * x) + 80 * cos(2 * x) - 9 * cos(x)) / 40.0
            val tz = (9 * cos(9 * x)) / 5.0

            val nx = -(968 * cos(11 * x) + 392 * cos(7 * x) + 225 * cos(5 * x) + 160 * cos(2 * x) + 9 * cos(x)) / 40.0
            val ny = (-968 * sin(11 * x) + 392 * sin(7 * x) - 225 * sin(5 * x) - 160 * sin(2 * x) + 9 * sin(x)) / 40.0
            val nz = -(81 * sin(9 * x)) / 5.0

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }

    fun decoratedKnot7a(segments: Int): List<KnotData> {
        return (0 until segments).map { i ->
            val x = i * 2.0 * PI / segments
            val px = cos(2.0 * x) * (1.0 + 0.4 * cos(7.0 * x) + 0.485 * cos(9 * x))
            val py = sin(2.0 * x) * (1.0 + 0.4 * cos(7.0 * x) + 0.485 * cos(9 * x))
            val pz = 0.3 * sin(9.0 * x)

            // Use https://www.derivative-calculator.net/ to calculate the derivatives
            val tx = -(1067 * sin(11 * x) + 720 * sin(9 * x) + 679 * sin(7 * x) + 400 * sin(5 * x) + 800 * sin(2 * x)) / 400.0
            val ty = (1067 * cos(11 * x) + 720 * cos(9 * x) - 679 * cos(7 * x) - 400 * cos(5 * x) + 800 * cos(2 * x)) / 400.0
            val tz = (27 * cos(9 * x)) / 10.0

            val nx = -(11737 * cos(11 * x) + 6480 * cos(9 * x) + 4753 * cos(7 * x) + 2000 * cos(5 * x) + 1600 * cos(2 * x)) / 400.0
            val ny = (-11737 * sin(11 * x) - 6480 * sin(9 * x) + 4753 * sin(7 * x) + 2000 * sin(5 * x) - 1600 * sin(2 * x)) / 400.0
            val nz = -(243 * sin(9 * x)) / 10.0

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }

    // Has a kink in it...
    fun decoratedKnot8c(segments: Int): List<KnotData> {
        return (0 until segments).map { i ->
            val x = i * 2.0 * PI / segments
            val px = cos(3.0 * x) * (1.0 + 0.35 * cos(6.0 * x) + 0.6 * sin(4 * x))
            val py = sin(3.0 * x) * (1.0 - 0.35 * cos(6.0 * x) + 0.6 * sin(4 * x))
            val pz = 0.2 * sin(8.0 * x)

            // Use https://www.derivative-calculator.net/ to calculate the derivatives
            val tx = -(63.0 * sin(9.0 * x) - 84.0 * cos(7.0 * x) + 141.0 * sin(3.0 * x) - 12.0 * cos(x)) / 40.0
            val ty = -(63.0 * cos(9.0 * x) - 84.0 * sin(7.0 * x) - 141.0 * cos(3.0 * x) + 12.0 * sin(x)) / 40.0
            val tz = (8.0 * cos(8.0 * x)) / 5.0

            val nx = -(567.0 * cos(9.0 * x) + 588.0 * sin(7.0 * x) + 423.0 * cos(3.0 * x) + 12.0 * sin(x)) / 40.0
            val ny = (567.0 * sin(9.0 * x) + 588.0 * cos(7.0 * x) - 423.0 * sin(3.0 * x) - 12.0 * cos(x)) / 40.0
            val nz = -(64.0 * sin(8.0 * x)) / 5.0

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }

    fun decoratedKnot13c(segments: Int): List<KnotData> {
        return (0 until segments).map { i ->
            val x = i * 2.0 * PI / segments
            val px = cos(3.0 * x) * (1.0 + 0.5 * cos(2.0 * x) + 0.75 * cos(10 * x))
            val py = sin(3.0 * x) * (1.0 + 0.5 * cos(2.0 * x) + 0.75 * cos(10 * x))
            val pz = 0.2 * sin(8.0 * x)

            // Use https://www.derivative-calculator.net/ to calculate the derivatives
            val tx = -(39 * sin(13 * x) + 21 * sin(7 * x) + 10 * sin(5 * x) + 24 * sin(3 * x) + 2 * sin(x)) / 8.0
            val ty = (39 * cos(13 * x) - 21 * cos(7 * x) + 10 * cos(5 * x) + 24 * cos(3 * x) + 2 * cos(x)) / 8.0
            val tz = (8.0 * cos(8.0 * x)) / 5.0

            val nx = -(507 * cos(13 * x) + 147 * cos(7 * x) + 50 * cos(5 * x) + 72 * cos(3 * x) + 2 * cos(x)) / 8.0
            val ny = -(507 * sin(13 * x) - 147 * sin(7 * x) + 50 * sin(5 * x) + 72 * sin(3 * x) + 2 * sin(x)) / 8.0
            val nz = -(64.0 * sin(8.0 * x)) / 5.0

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }


    // see https://www.gsn-lib.org/docs/nodes/MeshTorusNode.php
    fun torusKnot(p: Int, q: Int, a: Double, b: Double, scale: Double, segments: Int): List<KnotData> {
        return (0 until segments).map { i ->
            val x = i * 2 * PI / segments
            val px = (a + b * cos(q * x)) * cos(p * x) * scale
            val py = (a + b * cos(q * x)) * sin(p * x) * scale
            val pz = b * sin(q * x) * scale

            val nx = cos(p * x) * cos(q * x)
            val ny = sin(p * x) * cos(q * x)
            val nz = sin(q * x)

            val tx = p * (a + b * cos(q * x)) * (-sin(p * x)) + q * b * (-sin(q * x) * cos(p * x))
            val ty = p * (a + b * cos(q * x)) * (cos(p * x)) + q * b * (-sin(q * x) * sin(p * x))
            val tz = q * b * cos(q * x)

            createKnotData(px, pz, py, tx, tz, ty, nx, nz, ny)
        }
    }

    private fun createKnotData(px: Double, pz: Double, py: Double, tx: Double, tz: Double, ty: Double, nx: Double, nz: Double, ny: Double): KnotData {
        // Rotate by 90° in X axis to get torus flat on the XZ plane (swap Y with -Z)
        val vp = Vector3f(px.toFloat(), -pz.toFloat(), py.toFloat())
        val vt = Vector3f(tx.toFloat(), -tz.toFloat(), ty.toFloat()).normalize()
        val vn = Vector3f(nx.toFloat(), -nz.toFloat(), ny.toFloat()).normalize()
        return KnotData(
            point = vp,
            tangent = vt,
            normal = vn
        )
    }
}

data class KnotData(
    val point: Vector3f,
    val tangent: Vector3f,
    val normal: Vector3f
)