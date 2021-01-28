package net.fish.geometry.hex.projection

import net.fish.geometry.hex.Orientation.ORIENTATION
import net.fish.geometry.hex.WrappingHexGrid
import net.fish.geometry.paths.PathCreator

interface Surface {
    fun createMapper(): SurfaceMapper
    val mapper: SurfaceMapper
    var hexGrid: WrappingHexGrid
    val pathCreator: PathCreator
    var r: Float

    var gridWidth: Int
    var gridHeight: Int
    var gridOrientation: ORIENTATION
    var scale: Float
}