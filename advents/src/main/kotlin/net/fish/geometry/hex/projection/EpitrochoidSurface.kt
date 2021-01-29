package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.EpitrochoidPathCreator
import net.fish.geometry.paths.TorusKnotPathCreator

data class EpitrochoidSurface(
    override var gridWidth: Int,
    override var gridHeight: Int,
    override var gridOrientation: Orientation.ORIENTATION,
    var a: Float = 1.0f,
    var b: Float = 0.5f,
    var c: Float = 0.1f,
    override var r: Float = 0.2f,
    override var scale: Float = 1.0f
): Surface {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: EpitrochoidPathCreator
    override lateinit var mapper: SurfaceMapper

    override fun createMapper(): SurfaceMapper {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = EpitrochoidPathCreator(a, b, c, scale)
        mapper = SurfaceMapper(this)
        return mapper
    }
}

