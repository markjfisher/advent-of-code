package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class SimpleTorusPathCreator(
    val r: Float = 1.0f,
    val scale: Float = 1.0f
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return Knots.plainTorus(r.toDouble(), scale.toDouble(), segments)
    }
}