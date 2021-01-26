package net.fish.geometry.knots

import net.fish.geometry.paths.PathData
import net.fish.maths.Parametrics
import org.joml.Vector3f
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Knots {

    // see https://www.gsn-lib.org/docs/nodes/MeshTorusNode.php
    fun torusKnot(p: Int, q: Int, a: Double, b: Double, scale: Double, segments: Int): List<PathData> {
        val fns = Parametrics(
            { x -> (a + b * cos(q * x)) * cos(p * x) * scale },
            { x -> (a + b * cos(q * x)) * sin(p * x) * scale },
            { x -> b * sin(q * x) * scale }
        )
        return createPath(segments, fns)
    }

    fun trefoil(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> (cos(x) + 2.0 * cos(2.0 * x)) * scale },
            { x -> (sin(x) - 2.0 * sin(2.0 * x)) * scale },
            { x -> sin(3.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // slightly nicer starting position
    fun wikiTrefoil(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> (sin(x) + 2.0 * sin(2.0 * x)) * scale },
            { x -> (cos(x) - 2.0 * cos(2.0 * x)) * scale },
            { x -> -sin(3.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // Various decorated knots from https://www.mi.sanu.ac.rs/vismath/taylorapril2011/Taylor.pdf

    // Good!
    fun decoratedKnot4b(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> cos(2.0 * x) * (1.0 + 0.45 * cos(3.0 * x) + 0.4 * cos(9.0 * x)) * scale },
            { x -> sin(2.0 * x) * (1.0 + 0.45 * cos(3.0 * x) + 0.4 * cos(9.0 * x)) * scale },
            { x -> 0.2 * sin(9.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // Good
    fun decoratedKnot7a(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> cos(2.0 * x) * (1.0 + 0.4 * cos(7.0 * x) + 0.485 * cos(9.0 * x)) * scale },
            { x -> sin(2.0 * x) * (1.0 + 0.4 * cos(7.0 * x) + 0.485 * cos(9.0 * x)) * scale },
            { x -> 0.3 * sin(9.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // OK, some twisting but not too much
    fun decoratedKnot7b(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> cos(2.0 * x) * (1.0 + 0.475 * cos(4.0 * x) + 0.35 * cos(7.0 * x)) * scale },
            { x -> 1.55 * sin(2.0 * x) * (1.0 + 0.475 * cos(4.0 * x) + 0.35 * cos(7.0 * x)) * scale },
            { x -> 0.3 * sin(7.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // Really nice!
    fun decoratedKnot10b(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> 1.31 * cos(3.0 * x) * (1.0 + 0.5 * (cos(2.0 * x) - 0.35 * cos(4.0 * x))) * scale },
            { x -> sin(3.0 * x) * (1.0 + 0.85 * (cos(2.0 * x) - 0.35 * cos(4.0 * x))) * scale },
            { x -> 0.2 * sin(8.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // Adjusted z and this is now very nice
    fun decoratedKnot11c(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> cos(3.0 * x) * (1.0 + 0.65 * (cos(2.0 * x) + 0.36 * cos(5.0 * x))) * scale },
            { x -> sin(3.0 * x) * (1.0 + 0.65 * (cos(2.0 * x) + 0.36 * cos(5.0 * x))) * scale },
            { x -> 0.2 * sin(5.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    // bad folds and crossing
    fun decoratedKnot13c(segments: Int, scale: Double = 1.0): List<PathData> {
        val fns = Parametrics(
            { x -> cos(3.0 * x) * (1.0 + 0.5 * cos(2.0 * x) + 0.75 * cos(10 * x)) * scale },
            { x -> sin(3.0 * x) * (1.0 + 0.5 * cos(2.0 * x) + 0.75 * cos(10 * x)) * scale },
            { x -> 0.2 * sin(8.0 * x) * scale }
        )
        return createPath(segments, fns)
    }

    private fun createPath(segments: Int, fns: Parametrics) = (0 until segments).map { i ->
        val t = i * 2 * PI / segments
        createPathData(
            fns.fx.eval(t), fns.fy.eval(t), fns.fz.eval(t),
            fns.tx.eval(t), fns.ty.eval(t), fns.tz.eval(t),
            fns.nx.eval(t), fns.ny.eval(t), fns.nz.eval(t)
        )
    }

    private fun createPathData(px: Double, py: Double, pz: Double, tx: Double, ty: Double, tz: Double, nx: Double, ny: Double, nz: Double): PathData {
        // Rotate by 90° in X axis to get surface flat on the XZ plane (swap Y with -Z)
        return PathData(
            point = Vector3f(px.toFloat(), -pz.toFloat(), py.toFloat()),
            tangent = Vector3f(tx.toFloat(), -tz.toFloat(), ty.toFloat()).normalize(),
            normal = Vector3f(nx.toFloat(), -nz.toFloat(), ny.toFloat()).normalize()
        )
    }
}
