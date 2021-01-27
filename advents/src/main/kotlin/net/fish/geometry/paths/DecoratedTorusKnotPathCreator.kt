package net.fish.geometry.paths

import net.fish.geometry.knots.Knots

data class DecoratedTorusKnotPathCreator(
    val pattern: String,
    val scale: Double
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return when(pattern) {
            "4b" -> Knots.decoratedKnot4b(segments, scale)
            "7a" -> Knots.decoratedKnot7a(segments, scale)
            "7b" -> Knots.decoratedKnot7b(segments, scale)
            "10b" -> Knots.decoratedKnot10b(segments, scale)
            "11c" -> Knots.decoratedKnot11c(segments, scale)
            else -> throw Exception("Unknown pattern: $pattern")
        }

    }
}