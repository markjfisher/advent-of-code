package net.fish.geometry.paths

import net.fish.geometry.projection.DecoratedKnotType
import net.fish.geometry.projection.DecoratedKnotType.Type10b
import net.fish.geometry.projection.DecoratedKnotType.Type11c
import net.fish.geometry.projection.DecoratedKnotType.Type4b
import net.fish.geometry.projection.DecoratedKnotType.Type7a
import net.fish.geometry.projection.DecoratedKnotType.Type7b
import net.fish.geometry.knots.Knots

data class DecoratedTorusKnotPathCreator(
    var pattern: DecoratedKnotType,
    var scale: Float
): PathCreator {
    override fun createPath(segments: Int): List<PathData> {
        return when(pattern) {
            Type4b -> Knots.decoratedKnot4b(segments, scale.toDouble())
            Type7a -> Knots.decoratedKnot7a(segments, scale.toDouble())
            Type7b -> Knots.decoratedKnot7b(segments, scale.toDouble())
            Type10b -> Knots.decoratedKnot10b(segments, scale.toDouble())
            Type11c -> Knots.decoratedKnot11c(segments, scale.toDouble())
        }

    }
}