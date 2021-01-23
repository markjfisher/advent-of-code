package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class TorusKnotPathCreator(
    val p: Int,
    val q: Int,
    val a: Double = 1.0,
    val b: Double = 0.5,
    val scale: Double = 1.0,
    val segments: Int
): PathCreator {
    override fun createPath(): List<PathData> {
        return Knots.torusKnot(p, q, a, b, scale, segments)
    }
}