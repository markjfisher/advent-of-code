package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class TorusKnotPathCreator(
    val p: Int,
    val q: Int,
    val a: Float = 1.0f,
    val b: Float = 0.5f,
    val scale: Float = 1.0f
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return Knots.torusKnot(p, q, a.toDouble(), b.toDouble(), scale.toDouble(), segments)
    }
}