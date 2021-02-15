package net.fish.geometry.paths

import org.joml.Quaternionf
import org.joml.Vector3f

object CameraPath {
    fun generateCameraPath(pathCreator: PathCreator, width: Int, lookAhead: Int): List<CameraData> {
        // always calculate every third of a step for smoother animation, doesn't matter if we are pointy or flat.
        val pathData = pathCreator.createPath(width * 3)
        return pathData.mapIndexed { i, data ->
            val normal = data.normal
            val point = data.point
            // look ahead a number of points to find something to look at
            val nextPointDir = pathData[(i + lookAhead) % pathData.size].point.sub(point, Vector3f())
            val rotation = Quaternionf().lookAlong(nextPointDir, normal).normalize()
            CameraData(location = point, rotation = rotation)
        }
    }
}

data class CameraData(
    val location: Vector3f,
    val rotation: Quaternionf
)
