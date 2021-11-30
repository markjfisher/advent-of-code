package net.fish.geometry.projection

import net.fish.geometry.grid.GridType
import net.fish.geometry.hex.HexSurfaceMapper
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator
import net.fish.geometry.paths.EpitrochoidPathCreator
import net.fish.geometry.paths.PathCreator
import net.fish.geometry.paths.PathType
import net.fish.geometry.paths.PathType.DecoratedTorusKnot
import net.fish.geometry.paths.PathType.Epitrochoid
import net.fish.geometry.paths.PathType.SimpleTorus
import net.fish.geometry.paths.PathType.ThreeFactorParametric
import net.fish.geometry.paths.PathType.TorusKnot
import net.fish.geometry.paths.PathType.Trefoil
import net.fish.geometry.paths.SimpleTorusPathCreator
import net.fish.geometry.paths.ThreeFactorParametricPathCreator
import net.fish.geometry.paths.TorusKnotPathCreator
import net.fish.geometry.paths.TrefoilPathCreator
import net.fish.geometry.square.SquareSurfaceMapper
import net.fish.geometry.square.WrappingSquareGrid

data class Surface(
    var name: String,
    var surfaceData: MutableMap<String, String>,
    var pathType: PathType,
    var sweepRadius: Float,
    var scale: Float
) {
    fun createSurfaceMapper(): SurfaceMapper {
        val gridType = GridType.from(surfaceData["gridType"] ?: "UNKNOWN") ?: throw Exception("Unknown or missing gridType in data: $surfaceData")
        val pathCreator: PathCreator = createPath(pathType)
        return when(gridType) {
            GridType.HEX -> {
                val (m, n, orientation) = getHexGridConfig()
                val grid = WrappingHexGrid(m, n, Layout(orientation))
                HexSurfaceMapper(grid, pathCreator, sweepRadius)
            }
            GridType.SQUARE -> {
                val (width, height) = getSquareGridConfig()
                val grid = WrappingSquareGrid(width, height)
                SquareSurfaceMapper(grid, pathCreator, sweepRadius)
            }
        }
    }

    private fun createPath(pathType: PathType): PathCreator {
        return when (pathType) {
            SimpleTorus -> {
                val majorRadius = surfaceData["majorRadius"]?.toFloat() ?: throw Exception("Could not get majorRadius in data: $surfaceData")
                SimpleTorusPathCreator(majorRadius, scale)
            }
            Trefoil -> {
                TrefoilPathCreator(scale)
            }
            TorusKnot -> {
                val (p, q, a, b) = getTorusKnotConfig()
                TorusKnotPathCreator(p, q, a, b, scale)
            }
            DecoratedTorusKnot -> {
                val pattern = DecoratedKnotType.from(surfaceData["pattern"] ?: "Unknown") ?: throw Exception("No or unknown pattern specified in data: $surfaceData")
                DecoratedTorusKnotPathCreator(pattern, scale)
            }
            Epitrochoid -> {
                val (a, b, c) = getABCFloats()
                EpitrochoidPathCreator(a, b, c, scale)
            }
            ThreeFactorParametric -> {
                val (a, b, c) = getABCInts()
                ThreeFactorParametricPathCreator(a, b, c, scale)
            }
        }
    }

    private fun getABCFloats(): Triple<Float, Float, Float> {
        val a = surfaceData["a"]?.toFloat() ?: throw Exception("Missing 'a' value in data: $surfaceData")
        val b = surfaceData["b"]?.toFloat() ?: throw Exception("Missing 'b' value in data: $surfaceData")
        val c = surfaceData["c"]?.toFloat() ?: throw Exception("Missing 'c' value in data: $surfaceData")
        return Triple(a, b, c)
    }

    private fun getABCInts(): Triple<Int, Int, Int> {
        val a = surfaceData["a"]?.toInt() ?: throw Exception("Missing 'a' value in data: $surfaceData")
        val b = surfaceData["b"]?.toInt() ?: throw Exception("Missing 'b' value in data: $surfaceData")
        val c = surfaceData["c"]?.toInt() ?: throw Exception("Missing 'c' value in data: $surfaceData")
        return Triple(a, b, c)
    }

    private fun getHexGridConfig(): Triple<Int, Int, Orientation.ORIENTATION> {
        val width = surfaceData["width"]?.toInt() ?: throw Exception("Missing width values in data: $surfaceData")
        val height = surfaceData["height"]?.toInt() ?: throw Exception("Missing width values in data: $surfaceData")
        val orientation = Orientation.ORIENTATION.from(surfaceData["orientation"] ?: "Unknown") ?: throw Exception("Unknown or missing orientation in data: $surfaceData")
        return Triple(width, height, orientation)
    }

    private fun getTorusKnotConfig(): TorusKnotData {
        val p = surfaceData["p"]?.toInt() ?: throw Exception("Missing 'p' value in data: $surfaceData")
        val q = surfaceData["q"]?.toInt() ?: throw Exception("Missing 'q' value in data: $surfaceData")
        val a = surfaceData["a"]?.toFloat() ?: throw Exception("Missing 'a' value in data: $surfaceData")
        val b = surfaceData["b"]?.toFloat() ?: throw Exception("Missing 'b' value in data: $surfaceData")
        return TorusKnotData(p, q, a, b)
    }

    private fun getSquareGridConfig(): Pair<Int, Int> {
        val width = surfaceData["width"]?.toInt() ?: throw Exception("Missing width values in data: $surfaceData")
        val height = surfaceData["height"]?.toInt() ?: throw Exception("Missing width values in data: $surfaceData")
        return Pair(width, height)
    }
}

data class TorusKnotData(
    val p: Int,
    val q: Int,
    val a: Float,
    val b: Float
)