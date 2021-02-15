package net.fish.geometry.projection

import net.fish.geometry.hex.HexSurfaceMapperOld
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.TorusKnotPathCreator

data class TorusKnotSurface(
    override var gridWidth: Int,
    override var gridHeight: Int,
    override var gridOrientation: Orientation.ORIENTATION,
    var p: Int,
    var q: Int,
    var a: Float = 1.0f,
    var b: Float = 0.5f,
    override var r: Float = 0.2f,
    override var scale: Float = 1.0f
): SurfaceOld {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: TorusKnotPathCreator
    override lateinit var mapper: HexSurfaceMapperOld

    override fun createMapper(): HexSurfaceMapperOld {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = TorusKnotPathCreator(p, q, a, b, scale)
        mapper = HexSurfaceMapperOld(this)
        return mapper
    }
}

