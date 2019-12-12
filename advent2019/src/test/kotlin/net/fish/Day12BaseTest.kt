package net.fish

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class Day12BaseTest {
    val bodies = mapOf(
        "Io" to Day12Body(name = "Io", position = XYZ(x = -1, y = 0, z = 2), velocity = XYZ(x = 0, y = 0, z = 0)),
        "Europa" to Day12Body(name = "Europa", position = XYZ(x = 2, y = -10, z = -7), velocity = XYZ(x = 0, y = 0, z = 0)),
        "Ganymede" to Day12Body(name = "Ganymede", position = XYZ(x = 4, y = -8, z = 8), velocity = XYZ(x = 0, y = 0, z = 0)),
        "Callisto" to Day12Body(name = "Callisto", position = XYZ(x = 3, y = 5, z = -1), velocity = XYZ(x = 0, y = 0, z = 0))
    )

    @Test
    fun `running simulations`() {
        val day12A = Day12A()
        val mutableBodies = bodies.toMutableMap()
        day12A.runSimulation(mutableBodies, 1)
        assertThat(mutableBodies["Io"]).isEqualTo(Day12Body(position = XYZ(x = 2, y = -1, z = 1), velocity = XYZ(x = 3, y = -1, z = -1), name = "Io"))
        assertThat(mutableBodies["Europa"]).isEqualTo(Day12Body(position = XYZ(x = 3, y = -7, z = -4), velocity = XYZ(x = 1, y = 3, z = 3), name = "Europa"))
        assertThat(mutableBodies["Ganymede"]).isEqualTo(Day12Body(position = XYZ(x = 1, y = -7, z = 5), velocity = XYZ(x = -3, y = 1, z = -3), name = "Ganymede"))
        assertThat(mutableBodies["Callisto"]).isEqualTo(Day12Body(position = XYZ(x = 2, y = 2, z = 0), velocity = XYZ(x = -1, y = -3, z = 1), name = "Callisto"))

        day12A.runSimulation(mutableBodies, 1)
        assertThat(mutableBodies["Io"]).isEqualTo(Day12Body(position = XYZ(x = 5, y = -3, z = -1), velocity = XYZ(x = 3, y = -2, z = -2), name = "Io"))
        assertThat(mutableBodies["Europa"]).isEqualTo(Day12Body(position = XYZ(x = 1, y = -2, z = 2), velocity = XYZ(x = -2, y = 5, z = 6), name = "Europa"))
        assertThat(mutableBodies["Ganymede"]).isEqualTo(Day12Body(position = XYZ(x = 1, y = -4, z = -1), velocity = XYZ(x = 0, y = 3, z = -6), name = "Ganymede"))
        assertThat(mutableBodies["Callisto"]).isEqualTo(Day12Body(position = XYZ(x = 1, y = -4, z = 2), velocity = XYZ(x = -1, y = -6, z = 2), name = "Callisto"))

        // fast forward 8 more steps
        day12A.runSimulation(mutableBodies, 8)
        assertThat(mutableBodies["Io"]).isEqualTo(Day12Body(position = XYZ(x = 2, y = 1, z = -3), velocity = XYZ(x = -3, y = -2, z = 1), name = "Io"))
        assertThat(mutableBodies["Europa"]).isEqualTo(Day12Body(position = XYZ(x = 1, y = -8, z = 0), velocity = XYZ(x = -1, y = 1, z = 3), name = "Europa"))
        assertThat(mutableBodies["Ganymede"]).isEqualTo(Day12Body(position = XYZ(x = 3, y = -6, z = 1), velocity = XYZ(x = 3, y = 2, z = -3), name = "Ganymede"))
        assertThat(mutableBodies["Callisto"]).isEqualTo(Day12Body(position = XYZ(x = 2, y = 0, z = 4), velocity = XYZ(x = 1, y = -1, z = -1), name = "Callisto"))

        val energy = mutableBodies.map { it.value.energy() }.sum()
        assertThat(energy).isEqualTo(179)

    }
}