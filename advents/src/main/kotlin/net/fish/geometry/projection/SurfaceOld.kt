package net.fish.geometry.projection

import net.fish.geometry.hex.HexSurfaceMapperOld
import net.fish.geometry.hex.Orientation.ORIENTATION
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.PathCreator

interface SurfaceOld {
    fun createMapper(): HexSurfaceMapperOld
    val mapper: HexSurfaceMapperOld
    var hexGrid: WrappingHexGrid
    val pathCreator: PathCreator
    var r: Float

    var gridWidth: Int
    var gridHeight: Int
    var gridOrientation: ORIENTATION
    var scale: Float
}