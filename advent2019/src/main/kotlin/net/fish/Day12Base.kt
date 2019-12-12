package net.fish

import com.marcinmoskala.math.combinations
import kotlin.math.abs

open class Day12Base {
    companion object {
        val initialBodyState = mapOf(
            "Io" to Day12Body(
                name = "Io",
                position = XYZ(x = -16, y = -1, z = -12),
                velocity = XYZ(x = 0, y = 0, z = 0)
            ),
            "Europa" to Day12Body(
                name = "Europa",
                position = XYZ(x = 0, y = -4, z = -17),
                velocity = XYZ(x = 0, y = 0, z = 0)
            ),
            "Ganymede" to Day12Body(
                name = "Ganymede",
                position = XYZ(x = -11, y = 11, z = 0),
                velocity = XYZ(x = 0, y = 0, z = 0)
            ),
            "Callisto" to Day12Body(
                name = "Callisto",
                position = XYZ(x = 2, y = 2, z = -6),
                velocity = XYZ(x = 0, y = 0, z = 0)
            )
        )
    }


    fun runSimulation(bodies: MutableMap<String, Day12Body>, steps: Int = 1000) {
        (0 until steps).forEach { _ ->
            applyGravity(bodies)
            applyVelocity(bodies)
        }
    }

    private fun applyGravity(bodies: MutableMap<String, Day12Body>) {
        val combinations = bodies.entries.combinations(2)
        val deltas = combinations.map { pair ->
            val asList = pair.toList()
            val b1 = asList[0].value
            val b2 = asList[1].value

            val xd = diff(b1.position.x, b2.position.x)
            val yd = diff(b1.position.y, b2.position.y)
            val zd = diff(b1.position.z, b2.position.z)
            val d1 = Day12Delta(body = b1, delta = XYZ(x = xd, y = yd, z = zd))
            val d2 = Day12Delta(body = b2, delta = XYZ(x = -xd, y = -yd, z = -zd))

            listOf(d1, d2)
        }.flatten()

        deltas.forEach { d ->
            val entry = bodies[d.body.name]!!
            entry.velocity.x += d.delta.x
            entry.velocity.y += d.delta.y
            entry.velocity.z += d.delta.z
        }
    }

    private fun applyVelocity(bodies: MutableMap<String, Day12Body>) {
        bodies.entries.forEach { (_, body) ->
            body.position.x += body.velocity.x
            body.position.y += body.velocity.y
            body.position.z += body.velocity.z
        }
    }

    private fun diff(a: Int, b: Int) = if (a < b) 1 else if (a > b) -1 else 0

}

data class Day12Body(
    val name: String,
    var position: XYZ,
    var velocity: XYZ
) {
    private fun pot(): Int = abs(position.x) + abs(position.y) + abs(position.z)
    private fun kin(): Int = abs(velocity.x) + abs(velocity.y) + abs(velocity.z)
    fun energy(): Int = pot() * kin()
}

data class XYZ(
    var x: Int,
    var y: Int,
    var z: Int
)

data class Day12Delta(
    val body: Day12Body,
    var delta: XYZ
)