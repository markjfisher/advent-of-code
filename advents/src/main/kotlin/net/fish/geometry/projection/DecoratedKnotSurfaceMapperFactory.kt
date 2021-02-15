package net.fish.geometry.projection

import net.fish.geometry.grid.Grid
import net.fish.geometry.hex.HexSurfaceMapper
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator
import net.fish.geometry.square.SquareSurfaceMapper
import net.fish.geometry.square.WrappingSquareGrid

object DecoratedKnotSurfaceMapperFactory {
    fun createSurfaceMapper(
        grid: Grid,
        type: DecoratedKnotType,
        sweepRadius: Float,
        scale: Float
    ): SurfaceMapper {
        val pathCreator = DecoratedTorusKnotPathCreator(type, scale)
        return when(grid) {
            is WrappingHexGrid -> HexSurfaceMapper(grid = grid, pathCreator = pathCreator, sweepRadius = sweepRadius)
            is WrappingSquareGrid -> SquareSurfaceMapper(grid = grid, pathCreator = pathCreator, sweepRadius = sweepRadius)
            else -> throw Exception("Unknown grid type")
        }
    }

}