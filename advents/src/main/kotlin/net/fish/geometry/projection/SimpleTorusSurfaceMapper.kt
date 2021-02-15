package net.fish.geometry.projection

import net.fish.geometry.grid.Grid
import net.fish.geometry.hex.HexSurfaceMapper
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.SimpleTorusPathCreator
import net.fish.geometry.square.SquareSurfaceMapper
import net.fish.geometry.square.WrappingSquareGrid

object SimpleTorusSurfaceMapper {
    fun createSurfaceMapper(
        grid: Grid,
        majorRadius: Float,
        sweepRadius: Float,
        scale: Float
    ): SurfaceMapper {
        val pathCreator = SimpleTorusPathCreator(majorRadius, scale)
        return when(grid) {
            is WrappingHexGrid -> HexSurfaceMapper(grid, pathCreator, sweepRadius)
            is WrappingSquareGrid -> SquareSurfaceMapper(grid, pathCreator, sweepRadius)
            else -> throw Exception("Unknown grid type: $grid")
        }
    }
}