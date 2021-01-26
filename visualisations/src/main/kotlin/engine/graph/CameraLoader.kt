package engine.graph

import engine.Utils
import net.fish.geometry.paths.CameraData
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.math.PI

object CameraLoader {
    fun loadCamera(resourceName: String): List<CameraData> {
        // Read it per line, CSV style:
        // frame, locX, locY, locZ, eulX, eulY, eulZ
        // These are exported out of blender so will need translating to our -Z forward, Y up system
        return Utils.readAllLines(resourceName).map { line ->
            val (_, lx, ly, lz, rx, _, rz) = line.split(",").map { it.toFloat() }
            CameraData(
                location = Vector3f(lx, lz, -ly),
                // tests got this right! The blender camera 0 is pointing down, so have to lift x by pi/2.
                rotation = Quaternionf().rotationYXZ(rz, rx - (PI / 2.0).toFloat(), 0f).normalize().conjugate()
            )
        }
    }
}

operator fun <T> List<T>.component6(): T {
    return get(5)
}

operator fun <T> List<T>.component7(): T {
    return get(6)
}