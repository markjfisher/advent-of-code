package net.fish.geometry.projection

import net.fish.geometry.hex.HexSurfaceMapperOld
import net.fish.geometry.hex.Layout
import net.fish.geometry.hex.Orientation.ORIENTATION
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.DecoratedTorusKnotPathCreator

data class DecoratedKnotSurfaceOld(
    override var gridWidth: Int,
    override var gridHeight: Int,
    override var gridOrientation: ORIENTATION,
    var type: DecoratedKnotType,
    override var r: Float = 0.2f,
    override var scale: Float = 1.0f
): SurfaceOld {
    override lateinit var hexGrid: WrappingHexGrid
    override lateinit var pathCreator: DecoratedTorusKnotPathCreator
    override lateinit var mapper: HexSurfaceMapperOld

    override fun createMapper(): HexSurfaceMapperOld {
        hexGrid = WrappingHexGrid(gridWidth, gridHeight, Layout(gridOrientation))
        pathCreator = DecoratedTorusKnotPathCreator(type, scale)
        mapper = HexSurfaceMapperOld(this)
        return mapper
    }

}

enum class DecoratedKnotType {
    // Valid patterns: 4b, 7a, 7b, 10b, 11c
    Type4b, Type7a, Type7b, Type10b, Type11c;
    companion object {
        fun from(p: String): DecoratedKnotType? {
            return values().firstOrNull { it.name.equals(p, ignoreCase = true) }
        }
    }
}