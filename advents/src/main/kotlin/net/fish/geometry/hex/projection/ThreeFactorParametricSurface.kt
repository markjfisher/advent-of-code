package net.fish.geometry.hex.projection

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
): Surface {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: ThreeFactorParametricPathCreator
    override lateinit var mapper: SurfaceMapper

    override fun createMapper(): SurfaceMapper {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = ThreeFactorParametricPathCreator(a, b, c, scale)
        mapper = SurfaceMapper(this)
        return mapper
    }
}

