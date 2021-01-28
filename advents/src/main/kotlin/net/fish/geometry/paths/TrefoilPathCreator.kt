package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class TrefoilPathCreator(
    val scale: Float
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return Knots.wikiTrefoil(segments, scale.toDouble())
    }
}