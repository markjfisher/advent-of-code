package net.fish.geometry.paths

import net.fish.geometry.hex.projection.SurfaceMapper
import org.joml.Quaternionf
import org.joml.Vector3f

object CameraPath {
    fun generateCameraPath(surface: SurfaceMapper): List<CameraData> {
        // always calculate every half step for smoother animation, doesn't matter if we are pointy or flat.
        val pathData = surface.pathCreator.createPath(surface.hexGrid.m * 2)
        return pathData.mapIndexed { i, data ->
            val normal = data.normal
            val point = data.point
            // look ahead a number of points to find something to look at
            val nextPointDir = pathData[(i + 30) % pathData.size].point.sub(point, Vector3f())
            val rotation = Quaternionf().lookAlong(nextPointDir, normal).normalize()
            CameraData(location = point, rotation = rotation)
        }
    }
}

data class CameraData(
    val location: Vector3f,
    val rotation: Quaternionf
)
