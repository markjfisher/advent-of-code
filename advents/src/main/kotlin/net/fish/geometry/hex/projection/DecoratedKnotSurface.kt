package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator

data class DecoratedKnotSurface(
    override var gridWidth: Int,
    override var gridHeight: Int,
    override var gridOrientation: ORIENTATION,
    var type: DecoratedKnotType,
    override var r: Float = 0.2f,
    override var scale: Float = 1.0f
): Surface {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: DecoratedTorusKnotPathCreator
    override lateinit var mapper: SurfaceMapper

    override fun createMapper(): SurfaceMapper {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = DecoratedTorusKnotPathCreator(type, scale)
        mapper = SurfaceMapper(this)
        return mapper
    }

}

enum class DecoratedKnotType {
    // Valid patterns: 4b, 7a, 7b, 10b, 11c
    Type4b, Type7a, Type7b, Type10b, Type11c
}