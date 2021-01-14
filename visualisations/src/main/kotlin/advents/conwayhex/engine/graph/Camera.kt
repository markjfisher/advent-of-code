package advents.conwayhex.engine.graph

import org.joml.Math.toRadians
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.cos
import kotlin.math.sin

class Camera() {
    var position: Vector3f = Vector3f()
        private set

    var rotation: Quaternionf = Quaternionf()
        private set

    constructor(position: Vector3f, rotation: Quaternionf) : this() {
        this.position = position
        this.rotation = rotation
    }

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun movePosition(offsetX: Float, offsetY: Float, offsetZ: Float) {
        if (offsetZ != 0f) {
            position.x += sin(toRadians(rotation.y)) * -1.0f * offsetZ
            position.z += cos(toRadians(rotation.y)) * offsetZ
        }
        if (offsetX != 0f) {
            position.x += sin(toRadians(rotation.y - 90)) * -1.0f * offsetX
            position.z += cos(toRadians(rotation.y - 90)) * offsetX
        }
        position.y += offsetY
    }

    fun setRotation(w: Float, x: Float, y: Float, z: Float) {
        rotation.w = w
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }

    override fun toString(): String {
        return String.format("pos: $position, rot: $rotation (euler: ${rotation.getEulerAnglesXYZ(Vector3f())})")
    }

    fun moveRotation(offsetX: Float, offsetY: Float, offsetZ: Float) {
        rotation.rotateXYZ(offsetX / 30f, offsetY / 30f, offsetZ / 30f)
//        rotation.x = (rotation.x + offsetX)
//        rotation.y = (rotation.y + offsetY)
//        rotation.z = (rotation.z + offsetZ)
    }
}
