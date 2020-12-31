package advents.conwayhex.engine

import advents.conwayhex.engine.graph.Mesh
import org.joml.Vector3f

class GameItem(
    val mesh: Mesh,
    var position: Vector3f = Vector3f(),
    var scale: Float = 1f,
    var rotation: Vector3f = Vector3f(),
) {
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

    override fun toString(): String {
        return String.format("GameItem[position: %s, rotation: %s, scale: %f, mesh: %s]", position, rotation, scale, mesh)
    }
}
