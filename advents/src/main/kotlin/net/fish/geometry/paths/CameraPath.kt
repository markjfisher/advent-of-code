package net.fish.geometry.paths

import org.joml.Quaternionf
import org.joml.Vector3f

class CameraPath(private val pathCreator: PathCreator) {
    fun generateCameraPath(): List<CameraData> {
        val pathData = pathCreator.createPath()
        return pathData.mapIndexed { i, data ->
            val normal = data.normal
            val point = data.point
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
