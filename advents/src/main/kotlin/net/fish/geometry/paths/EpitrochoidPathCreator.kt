package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class EpitrochoidPathCreator(
    val a: Float = 1.0f,
    val b: Float = 0.5f,
    val c: Float = 0.2f,
    val scale: Float = 1.0f
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return Knots.epitrochoid(a.toDouble(), b.toDouble(), c.toDouble(), scale.toDouble(), segments)
    }
}