package advents.conwayhex.engine

import advents.conwayhex.engine.graph.Mesh
import org.joml.Vector3f

class GameItem(val mesh: Mesh) {
    val position: Vector3f = Vector3f()
    var scale: Float = 1f
    val rotation: Vector3f = Vector3f()

    fun setPosition(x: Float, y: Float, z: Float) {
        position.x = x
        position.y = y
        position.z = z
    }

    fun setRotation(x: Float, y: Float, z: Float) {
        rotation.x = x
        rotation.y = y
        rotation.z = z
    }
}
