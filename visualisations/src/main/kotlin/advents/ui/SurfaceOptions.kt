package advents.ui

import net.fish.geometry.paths.PathType
import net.fish.geometry.projection.Surface

data class SurfaceOptions(
    var globalAlpha: Float,
    var animationPercentages: MutableMap<Int, Float> = defaultAnimationPercentages,
    var surface: Surface,
    var surfaces: List<Surface>
) {
    companion object {
        // y = 1648x / (1250x + 625), or y = (n π + 2) x / ( π (n x + 1)). higher n = steeper drop/climb
        val defaultAnimationPercentages = mutableMapOf(
            0 to 0f,
            10 to 0.3152f,
            20 to 0.5087f,
            30 to 0.6397f,
            40 to 0.7342f,
            50 to 0.8056f,
            60 to 0.8614f,
            70 to 0.9063f,
            80 to 0.9431f,
            90 to 0.9739f,
            100 to 1f
        )

        val defaultSurfaces = listOf(
            Surface("(Hex) 3,7 Torus Knot", mutableMapOf("gridType" to "hex", "width" to "800", "height" to "16", "orientation" to "pointy", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),
            Surface("(Square) 3,7 Torus Knot", mutableMapOf("gridType" to "square", "width" to "800", "height" to "16", "p" to "3", "q" to "7", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),
            Surface("(Hex) 11,17 Torus Knot", mutableMapOf("gridType" to "hex", "width" to "1100", "height" to "12", "orientation" to "pointy", "p" to "11", "q" to "17", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),
            Surface("(Square) 11,17 Torus Knot", mutableMapOf("gridType" to "square", "width" to "1100", "height" to "13", "p" to "11", "q" to "17", "a" to "1.0", "b" to "0.2"), PathType.TorusKnot, 0.2f, 5.0f),
            Surface("(Hex) Torus", mutableMapOf("gridType" to "hex", "width" to "160", "height" to "50", "orientation" to "pointy", "majorRadius" to "8.0f"), PathType.SimpleTorus, 1.5f, 1.0f),
            Surface("(Square) Torus", mutableMapOf("gridType" to "square", "width" to "160", "height" to "50", "majorRadius" to "8.0f"), PathType.SimpleTorus, 1.5f, 1.0f),
            Surface("(Hex) 10b Decorated Torus Knot", mutableMapOf("gridType" to "hex", "width" to "900", "height" to "16", "orientation" to "pointy", "pattern" to "Type10b"), PathType.DecoratedTorusKnot, 0.25f, 5.0f),
            Surface("(Square) 10b Decorated Torus Knot", mutableMapOf("gridType" to "square", "width" to "900", "height" to "16", "pattern" to "Type10b"), PathType.DecoratedTorusKnot, 0.25f, 5.0f),
            Surface("(Hex) Trefoil", mutableMapOf("gridType" to "hex", "width" to "600", "height" to "26", "orientation" to "pointy"), PathType.Trefoil, 0.6f, 3.0f),
            Surface("(Square) Trefoil", mutableMapOf("gridType" to "square", "width" to "600", "height" to "26"), PathType.Trefoil, 0.6f, 3.0f),
            Surface("(Hex) 3 Factor Parametric", mutableMapOf("gridType" to "hex", "width" to "1000", "height" to "12", "orientation" to "pointy", "a" to "2", "b" to "5", "c" to "4"), PathType.Trefoil, 0.3f, 3.0f),
            Surface("(Square) 3 Factor Parametric", mutableMapOf("gridType" to "square", "width" to "1000", "height" to "12", "a" to "2", "b" to "5", "c" to "4"), PathType.Trefoil, 0.3f, 3.0f),
            Surface("(Hex) Epitrochoid", mutableMapOf("gridType" to "hex", "width" to "1000", "height" to "12", "orientation" to "pointy", "a" to "5.0", "b" to "1.0", "c" to "3.5"), PathType.Epitrochoid, 0.3f, 3.0f),
            Surface("(Square) Epitrochoid", mutableMapOf("gridType" to "square", "width" to "1000", "height" to "12", "a" to "5.0", "b" to "1.0", "c" to "3.5"), PathType.Epitrochoid, 0.3f, 3.0f),
            Surface("(Square) Simple Grid", mutableMapOf("gridType" to "square", "width" to "10", "height" to "10"), PathType.StaticPoint, 0f, 1f)
        )

    }

}


