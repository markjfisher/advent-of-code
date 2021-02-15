package net.fish.geometry.projection

import net.fish.geometry.hex.HexSurfaceMapperOld
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.ThreeFactorParametricPathCreator

data class ThreeFactorParametricSurface(
    override var gridWidth: Int,
    override var gridHeight: Int,
    override var gridOrientation: Orientation.ORIENTATION,
    var a: Int = 8,
    var b: Int = 4,
    var c: Int = 12,
    override var r: Float = 0.2f,
    override var scale: Float = 1.0f
): SurfaceOld {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: ThreeFactorParametricPathCreator
    override lateinit var mapper: HexSurfaceMapperOld

    override fun createMapper(): HexSurfaceMapperOld {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = ThreeFactorParametricPathCreator(a, b, c, scale)
        mapper = HexSurfaceMapperOld(this)
        return mapper
    }
}

