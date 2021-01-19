package engine.graph

import org.joml.Quaternionf
import org.joml.Vector3f

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

    fun setRotation(w: Float, x: Float, y: Float, z: Float) {
        rotation.w = w
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }

    override fun toString(): String {
        return String.format("pos: $position, rot: $rotation (euler: ${rotation.getEulerAnglesXYZ(Vector3f())})")
    }

}
