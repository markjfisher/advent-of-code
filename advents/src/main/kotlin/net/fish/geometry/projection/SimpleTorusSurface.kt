package net.fish.geometry.projection

import net.fish.geometry.hex.HexSurfaceMapperOld
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.SimpleTorusPathCreator

data class SimpleTorusSurface(
    override var gridWidth: Int,
    override var gridHeight: Int,
    override var gridOrientation: ORIENTATION,
    var majorRadius: Float,
    override var r: Float = 0.2f,
    override var scale: Float = 1.0f
): SurfaceOld {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: SimpleTorusPathCreator
    override lateinit var mapper: HexSurfaceMapperOld

    override fun createMapper(): HexSurfaceMapperOld {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = SimpleTorusPathCreator(majorRadius, scale)
        mapper = HexSurfaceMapperOld(this)
        return mapper
    }
}

