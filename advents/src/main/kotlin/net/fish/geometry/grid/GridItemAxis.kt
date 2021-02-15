package net.fish.geometry.grid

import org.joml.Matrix3f
import org.joml.Vector3f

data class GridItemAxis(
    val location: Vector3f,
    val axes: Matrix3f // the columns of this matrix are the 3 bases of the Hex, with z pointing out of the hex
) {
    override fun toString(): String {
        return String.format("GridItemAxis[\nlocation: %s\naxes:\n%s\n]", location, axes)
    }
}
