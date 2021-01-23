package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class TrefoilPathCreator(
    val scale: Double,
    val segments: Int
): PathCreator {
    override fun createPath(): List<PathData> {
        return Knots.wikiTrefoil(segments, scale)
    }
}