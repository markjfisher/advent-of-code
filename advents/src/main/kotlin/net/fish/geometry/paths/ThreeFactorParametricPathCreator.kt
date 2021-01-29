package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class ThreeFactorParametricPathCreator(
    val a: Int = 8,
    val b: Int = 4,
    val c: Int = 12,
    val scale: Float = 1.0f
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return Knots.threeFactorParametric(a, b, c, scale.toDouble(), segments)
    }
}