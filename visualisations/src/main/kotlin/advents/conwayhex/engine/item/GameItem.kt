package advents.conwayhex.engine.item

import advents.conwayhex.engine.graph.Mesh
import org.joml.Quaternionf
import org.joml.Vector3f

class GameItem(
    val mesh: Mesh,
    val position: Vector3f = Vector3f(),
    var scale: Float = 1f,
    val rotation: Quaternionf = Quaternionf(),
    var colour: Vector3f = mesh.colour
) {
    fun setPosition(x: Float, y: Float, z: Float) {
        position.set(x, y, z)
    }

    fun setPosition(v: Vector3f) {
        position.set(v)
    }

    fun setRotation(q: Quaternionf) {
        rotation.set(q)
    }

    override fun toString(): String {
        return String.format("GameItem[position: %s, rotation: %s, scale: %f, mesh: %s]", position, rotation, scale, mesh)
    }
}
